/*
 * SimpleAudioConversion.java
 * Copyright (C) 2017 Radiodef
 */

package adams.data.audio;

import adams.core.License;
import adams.core.annotation.ThirdPartyCopyright;
import gnu.trove.list.TFloatList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.signum;

/**
 * Performs rudimentary audio format conversion.
 * <p>
 * Example usage:
 *
 * <pre>{@code
 * AudioInputStream ais = ... ;
 * SourceDataLine  line = ... ;
 * AudioFormat      fmt = ... ;
 *
 * // do prep
 *
 * for (int blen = 0; (blen = ais.read(bytes)) > -1;) {
 *     int slen;
 *     slen = SimpleAudioConversion.unpack(bytes, samples, blen, fmt);
 *
 *     // do something with samples
 *
 *     blen = SimpleAudioConversion.pack(samples, bytes, slen, fmt);
 *     line.write(bytes, 0, blen);
 * }
 * }</pre>
 *
 * @author Radiodef
 * @see <a href="http://stackoverflow.com/a/26824664/2891664">Overview on StackOverflow.com</a>
 */
@ThirdPartyCopyright(
  license = License.CC_BY_SA_3,
  author = "Radiodef",
  url = "https://stackoverflow.com/a/26824664/4698227"
)
public class SimpleAudioConversion {
  private SimpleAudioConversion() {}

  /**
   * Converts:
   * <ul>
   * <li>from a byte array ({@code byte[]})
   * <li>to an audio sample array ({@code float[]}).
   * </ul>
   *
   * @param bytes   the byte array, filled by the {@code InputStream}.
   * @param samples an array to fill up with audio samples.
   * @param blen    the return value of {@code InputStream.read}.
   * @param fmt     the source {@code AudioFormat}.
   *
   * @return the number of valid audio samples converted.
   *
   * @throws NullPointerException
   *  if {@code bytes}, {@code samples} or {@code fmt} is {@code null}
   * @throws ArrayIndexOutOfBoundsException
   *  if {@code (bytes.length < blen)}
   *  or {@code (samples.length < blen / bytesPerSample(fmt.getBitsPerSample()))}.
   */
  public static int unpack(byte[]      bytes,
			   TFloatList  samples,
			   int         blen,
			   AudioFormat fmt) {
    int   bitsPerSample = fmt.getSampleSizeInBits();
    int  bytesPerSample = bytesPerSample(bitsPerSample);
    boolean isBigEndian = fmt.isBigEndian();
    Encoding   encoding = fmt.getEncoding();
    double    fullScale = fullScale(bitsPerSample);

    int i = 0;
    int s = 0;
    while (i < blen) {
      long temp = unpackBits(bytes, i, isBigEndian, bytesPerSample);
      float sample = 0f;

      if (encoding == Encoding.PCM_SIGNED) {
	temp = extendSign(temp, bitsPerSample);
	sample = (float) (temp / fullScale);

      } else if (encoding == Encoding.PCM_UNSIGNED) {
	temp = signUnsigned(temp, bitsPerSample);
	sample = (float) (temp / fullScale);

      } else if (encoding == Encoding.PCM_FLOAT) {
	if (bitsPerSample == 32) {
	  sample = Float.intBitsToFloat((int) temp);
	} else if (bitsPerSample == 64) {
	  sample = (float) Double.longBitsToDouble(temp);
	}
      } else if (encoding == Encoding.ULAW) {
	sample = bitsToMuLaw(temp);

      } else if (encoding == Encoding.ALAW) {
	sample = bitsToALaw(temp);
      }

      samples.add(sample);

      i += bytesPerSample;
      s++;
    }

    return s;
  }

  /**
   * Converts:
   * <ul>
   * <li>from an audio sample array ({@code float[]})
   * <li>to a byte array ({@code byte[]}).
   * </ul>
   *
   * @param samples an array of audio samples to encode.
   * @param bytes   an array to fill up with bytes.
   * @param slen    the return value of {@code unpack}.
   * @param fmt     the destination {@code AudioFormat}.
   *
   * @return the number of valid bytes converted.
   *
   * @throws NullPointerException
   *  if {@code samples}, {@code bytes} or {@code fmt} is {@code null}
   * @throws ArrayIndexOutOfBoundsException
   *  if {@code(samples.length < slen)}
   *  or {@code (bytes.length < slen * bytesPerSample(fmt.getSampleSizeInBits()))}
   */
  public static int pack(float[]     samples,
			 byte[]      bytes,
			 int         slen,
			 AudioFormat fmt) {
    int   bitsPerSample = fmt.getSampleSizeInBits();
    int  bytesPerSample = bytesPerSample(bitsPerSample);
    boolean isBigEndian = fmt.isBigEndian();
    Encoding   encoding = fmt.getEncoding();
    double    fullScale = fullScale(bitsPerSample);

    int i = 0;
    int s = 0;
    while (s < slen) {
      float sample = samples[s];
      long temp = 0L;

      if (encoding == Encoding.PCM_SIGNED) {
	temp = (long) (sample * fullScale);

      } else if (encoding == Encoding.PCM_UNSIGNED) {
	temp = (long) (sample * fullScale);
	temp = unsignSigned(temp, bitsPerSample);

      } else if (encoding == Encoding.PCM_FLOAT) {
	if (bitsPerSample == 32) {
	  temp = Float.floatToRawIntBits(sample);
	} else if (bitsPerSample == 64) {
	  temp = Double.doubleToRawLongBits(sample);
	}
      } else if (encoding == Encoding.ULAW) {
	temp = muLawToBits(sample);

      } else if (encoding == Encoding.ALAW) {
	temp = aLawToBits(sample);
      }

      packBits(bytes, i, temp, isBigEndian, bytesPerSample);

      i += bytesPerSample;
      s++;
    }

    return i;
  }

  /**
   * Computes the block-aligned bytes per sample of the audio format,
   * with {@code (int) ceil(bitsPerSample / 8.0)}.
   * <p>
   * This is generally equivalent to the optimization
   * {@code ((bitsPerSample + 7) >>> 3)}. (Except for
   * the invalid argument {@code bitsPerSample <= 0}.)
   * <p>
   * Round towards the ceiling because formats that allow bit depths
   * in non-integral multiples of 8 typically pad up to the nearest
   * integral multiple of 8. So for example, a 31-bit AIFF file will
   * actually store 32-bit blocks.
   *
   * @param  bitsPerSample the return value of {@code AudioFormat.getSampleSizeInBits}.
   * @return The block-aligned bytes per sample of the audio format.
   */
  public static int bytesPerSample(int bitsPerSample) {
    return (int) ceil(bitsPerSample / 8.0);
  }

  /**
   * Computes the largest magnitude representable by the audio format,
   * with {@code pow(2.0, bitsPerSample - 1)}.
   * <p>
   * For {@code bitsPerSample < 64}, this is generally equivalent to
   * the optimization {@code (1L << (bitsPerSample - 1L))}. (Except for
   * the invalid argument {@code bitsPerSample <= 0}.)
   * <p>
   * The result is returned as a {@code double} because, in the case that
   * {@code bitsPerSample == 64}, a {@code long} would overflow.
   *
   * @param bitsPerSample the return value of {@code AudioFormat.getBitsPerSample}.
   * @return the largest magnitude representable by the audio format.
   */
  public static double fullScale(int bitsPerSample) {
    return pow(2.0, bitsPerSample - 1);
  }

  private static long unpackBits(byte[]  bytes,
				 int     i,
				 boolean isBigEndian,
				 int     bytesPerSample) {
    switch (bytesPerSample) {
      case  1: return unpack8Bit(bytes, i);
      case  2: return unpack16Bit(bytes, i, isBigEndian);
      case  3: return unpack24Bit(bytes, i, isBigEndian);
      default: return unpackAnyBit(bytes, i, isBigEndian, bytesPerSample);
    }
  }

  private static long unpack8Bit(byte[] bytes, int i) {
    return bytes[i] & 0xffL;
  }

  private static long unpack16Bit(byte[]  bytes,
				  int     i,
				  boolean isBigEndian) {
    if (isBigEndian) {
      return (
	((bytes[i    ] & 0xffL) << 8L)
	  |  (bytes[i + 1] & 0xffL)
      );
    } else {
      return (
	(bytes[i    ] & 0xffL)
	  | ((bytes[i + 1] & 0xffL) << 8L)
      );
    }
  }

  private static long unpack24Bit(byte[]  bytes,
				  int     i,
				  boolean isBigEndian) {
    if (isBigEndian) {
      return (
	((bytes[i    ] & 0xffL) << 16L)
	  | ((bytes[i + 1] & 0xffL) <<  8L)
	  |  (bytes[i + 2] & 0xffL)
      );
    } else {
      return (
	(bytes[i    ] & 0xffL)
	  | ((bytes[i + 1] & 0xffL) <<  8L)
	  | ((bytes[i + 2] & 0xffL) << 16L)
      );
    }
  }

  private static long unpackAnyBit(byte[]  bytes,
				   int     i,
				   boolean isBigEndian,
				   int     bytesPerSample) {
    long temp = 0L;

    if (isBigEndian) {
      for (int b = 0; b < bytesPerSample; b++) {
	temp |= (bytes[i + b] & 0xffL) << (
	  8L * (bytesPerSample - b - 1L)
	);
      }
    } else {
      for (int b = 0; b < bytesPerSample; b++) {
	temp |= (bytes[i + b] & 0xffL) << (8L * b);
      }
    }

    return temp;
  }

  private static void packBits(byte[]  bytes,
			       int     i,
			       long    temp,
			       boolean isBigEndian,
			       int     bytesPerSample) {
    switch (bytesPerSample) {
      case  1: pack8Bit(bytes, i, temp);
	break;
      case  2: pack16Bit(bytes, i, temp, isBigEndian);
	break;
      case  3: pack24Bit(bytes, i, temp, isBigEndian);
	break;
      default: packAnyBit(bytes, i, temp, isBigEndian, bytesPerSample);
	break;
    }
  }

  private static void pack8Bit(byte[] bytes, int i, long temp) {
    bytes[i] = (byte) (temp & 0xffL);
  }

  private static void pack16Bit(byte[]  bytes,
				int     i,
				long    temp,
				boolean isBigEndian) {
    if (isBigEndian) {
      bytes[i    ] = (byte) ((temp >>> 8L) & 0xffL);
      bytes[i + 1] = (byte) ( temp         & 0xffL);
    } else {
      bytes[i    ] = (byte) ( temp         & 0xffL);
      bytes[i + 1] = (byte) ((temp >>> 8L) & 0xffL);
    }
  }

  private static void pack24Bit(byte[]  bytes,
				int     i,
				long    temp,
				boolean isBigEndian) {
    if (isBigEndian) {
      bytes[i    ] = (byte) ((temp >>> 16L) & 0xffL);
      bytes[i + 1] = (byte) ((temp >>>  8L) & 0xffL);
      bytes[i + 2] = (byte) ( temp          & 0xffL);
    } else {
      bytes[i    ] = (byte) ( temp          & 0xffL);
      bytes[i + 1] = (byte) ((temp >>>  8L) & 0xffL);
      bytes[i + 2] = (byte) ((temp >>> 16L) & 0xffL);
    }
  }

  private static void packAnyBit(byte[]  bytes,
				 int     i,
				 long    temp,
				 boolean isBigEndian,
				 int     bytesPerSample) {
    if (isBigEndian) {
      for (int b = 0; b < bytesPerSample; b++) {
	bytes[i + b] = (byte) (
	  (temp >>> (8L * (bytesPerSample - b - 1L))) & 0xffL
	);
      }
    } else {
      for (int b = 0; b < bytesPerSample; b++) {
	bytes[i + b] = (byte) ((temp >>> (8L * b)) & 0xffL);
      }
    }
  }

  private static long extendSign(long temp, int bitsPerSample) {
    int extensionBits = 64 - bitsPerSample;
    return (temp << extensionBits) >> extensionBits;
  }

  private static long signUnsigned(long temp, int bitsPerSample) {
    return temp - (long) fullScale(bitsPerSample);
  }

  private static long unsignSigned(long temp, int bitsPerSample) {
    return temp + (long) fullScale(bitsPerSample);
  }

  // mu-law constant
  private static final double MU = 255.0;
  // A-law constant
  private static final double A = 87.7;
  // reciprocal of A
  private static final double RE_A = 1.0 / A;
  // natural logarithm of A
  private static final double LN_A = log(A);
  // if values are below this, the A-law exponent is 0
  private static final double EXP_0 = 1.0 / (1.0 + LN_A);

  private static float bitsToMuLaw(long temp) {
    temp ^= 0xffL;
    if ((temp & 0x80L) == 0x80L) {
      temp = -(temp ^ 0x80L);
    }

    float sample = (float) (temp / fullScale(8));

    return (float) (
      signum(sample)
	*
	(1.0 / MU)
	*
	(pow(1.0 + MU, abs(sample)) - 1.0)
    );
  }

  private static long muLawToBits(float sample) {
    double sign = signum(sample);
    sample = abs(sample);

    sample = (float) (
      sign * (log(1.0 + (MU * sample)) / log(1.0 + MU))
    );

    long temp = (long) (sample * fullScale(8));

    if (temp < 0L) {
      temp = -temp ^ 0x80L;
    }

    return temp ^ 0xffL;
  }

  private static float bitsToALaw(long temp) {
    temp ^= 0x55L;
    if ((temp & 0x80L) == 0x80L) {
      temp = -(temp ^ 0x80L);
    }

    float sample = (float) (temp / fullScale(8));

    float sign = signum(sample);
    sample = abs(sample);

    if (sample < EXP_0) {
      sample = (float) (sample * ((1.0 + LN_A) / A));
    } else {
      sample = (float) (exp((sample * (1.0 + LN_A)) - 1.0) / A);
    }

    return sign * sample;
  }

  private static long aLawToBits(float sample) {
    double sign = signum(sample);
    sample = abs(sample);

    if (sample < RE_A) {
      sample = (float) ((A * sample) / (1.0 + LN_A));
    } else {
      sample = (float) ((1.0 + log(A * sample)) / (1.0 + LN_A));
    }

    sample *= sign;

    long temp = (long) (sample * fullScale(8));

    if (temp < 0L) {
      temp = -temp ^ 0x80L;
    }

    return temp ^ 0x55L;
  }
}