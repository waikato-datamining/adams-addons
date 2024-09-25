package adams.flow.maven.shared;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;

/**
 * The ADAMS Maven Plugin needs to fiddle with the filesystem, to create and optionally prune
 * directories or detect/create various files. This utility class contains all such algorithms, and serves as
 * an entry point to any Plexus Utils methods.
 * Based on jaxb2-maven-plugin code.
 *
 * @author <a href="mailto:lj@jguru.se">Lennart J&ouml;relid</a>
 */
public final class FileSystemUtilities {

    /*
     * Hide the constructor for utility classes.
     */
    private FileSystemUtilities() {
        // Do nothing
    }

    /**
     * FileFilter which accepts Files that exist and for which {@code File.isFile() } is {@code true}.
     */
    public static final FileFilter EXISTING_FILE = new FileFilter() {
        @Override
        public boolean accept(final File candidate) {
            return candidate != null && candidate.exists() && candidate.isFile();
        }
    };

    /**
     * FileFilter which accepts Files that exist and for which {@code File.isDirectory() } is {@code true}.
     */
    public static final FileFilter EXISTING_DIRECTORY = new FileFilter() {
        @Override
        public boolean accept(final File candidate) {
            return candidate != null && candidate.exists() && candidate.isDirectory();
        }
    };

    /**
     * Acquires the canonical path for the supplied file.
     *
     * @param file A non-null File for which the canonical path should be retrieved.
     * @return The canonical path of the supplied file.
     */
    public static String getCanonicalPath(final File file) {
        return getCanonicalFile(file).getPath();
    }

    /**
     * Non-valid Characters for naming files, folders under Windows: <code>":", "*", "?", "\"", "<", ">", "|"</code>
     *
     * @see <a href="http://support.microsoft.com/?scid=kb%3Ben-us%3B177506&x=12&y=13">
     * http://support.microsoft.com/?scid=kb%3Ben-us%3B177506&x=12&y=13</a>
     * @see FileUtils
     */
    private static final String[] INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME = {":", "*", "?", "\"", "<", ">", "|"};

    /**
     * Acquires the canonical File for the supplied file.
     *
     * @param file A non-null File for which the canonical File should be retrieved.
     * @return The canonical File of the supplied file.
     */
    public static File getCanonicalFile(final File file) {

        // Check sanity
        Validate.notNull(file, "file");

        // All done
        try {
            return file.getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not acquire the canonical file for ["
                    + file.getAbsolutePath() + "]", e);
        }
    }

    /**
     * <p>Retrieves the canonical File matching the supplied path in the following order or priority:</p>
     * <ol>
     * <li><strong>Absolute path:</strong> The path is used by itself (i.e. {@code new File(path);}). If an
     * existing file or directory matches the provided path argument, its canonical path will be returned.</li>
     * <li><strong>Relative path:</strong> The path is appended to the baseDir (i.e.
     * {@code new File(baseDir, path);}). If an existing file or directory matches the provided path argument,
     * its canonical path will be returned. Only in this case will be baseDir argument be considered.</li>
     * </ol>
     * <p>If no file or directory could be derived from the supplied path and baseDir, {@code null} is returned.</p>
     *
     * @param path    A non-null path which will be used to find an existing file or directory.
     * @param baseDir A directory to which the path will be appended to search for the existing file or directory in
     *                case the file was nonexistent when interpreted as an absolute path.
     * @return either a canonical File for the path, or {@code null} if no file or directory matched
     * the supplied path and baseDir.
     */
    public static File getExistingFile(final String path, final File baseDir) {

        // Check sanity
        Validate.notEmpty(path, "path");
        final File theFile = new File(path);
        File toReturn = null;

        // Is 'path' absolute?
        if (theFile.isAbsolute() && (EXISTING_FILE.accept(theFile) || EXISTING_DIRECTORY.accept(theFile))) {
            toReturn = getCanonicalFile(theFile);
        }

        // Is 'path' relative?
        if (!theFile.isAbsolute()) {

            // In this case, baseDir cannot be null.
            Validate.notNull(baseDir, "baseDir");
            final File relativeFile = new File(baseDir, path);

            if (EXISTING_FILE.accept(relativeFile) || EXISTING_DIRECTORY.accept(relativeFile)) {
                toReturn = getCanonicalFile(relativeFile);
            }
        }

        // The path provided did not point to an existing File or Directory.
        return toReturn;
    }

    /**
     * Retrieves the URL for the supplied File. Convenience method which hides exception handling
     * for the operation in question.
     *
     * @param aFile A File for which the URL should be retrieved.
     * @return The URL for the supplied aFile.
     * @throws IllegalArgumentException if getting the URL yielded a MalformedURLException.
     */
    public static URL getUrlFor(final File aFile) throws IllegalArgumentException {

        // Check sanity
        Validate.notNull(aFile, "aFile");

        try {
            return aFile.toURI().normalize().toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Could not retrieve the URL from file ["
                    + getCanonicalPath(aFile) + "]", e);
        }
    }

    /**
     * Acquires the file for a supplied URL, provided that its protocol is is either a file or a jar.
     *
     * @param anURL    a non-null URL.
     * @param encoding The encoding to be used by the URLDecoder to decode the path found.
     * @return The File pointing to the supplied URL, for file or jar protocol URLs and null otherwise.
     */
    public static File getFileFor(final URL anURL, final String encoding) {

        // Check sanity
        Validate.notNull(anURL, "anURL");
        Validate.notNull(encoding, "encoding");

        final String protocol = anURL.getProtocol();
        File toReturn = null;
        if ("file".equalsIgnoreCase(protocol)) {
            try {
                final String decodedPath = URLDecoder.decode(anURL.getPath(), encoding);
                toReturn = new File(decodedPath);
            } catch (Exception e) {
                throw new IllegalArgumentException("Could not get the File for [" + anURL + "]", e);
            }
        } else if ("jar".equalsIgnoreCase(protocol)) {

            try {

                // Decode the JAR
                final String tmp = URLDecoder.decode(anURL.getFile(), encoding);

                // JAR URLs generally contain layered protocols, such as:
                // jar:file:/some/path/to/nazgul-tools-validation-aspect-4.0.2.jar!/the/package/ValidationAspect.class
                final URL innerURL = new URI(tmp).toURL();

                // We can handle File protocol URLs here.
                if ("file".equalsIgnoreCase(innerURL.getProtocol())) {

                    // Peel off the inner protocol
                    final String innerUrlPath = innerURL.getPath();
                    final String filePath = innerUrlPath.contains("!")
                            ? innerUrlPath.substring(0, innerUrlPath.indexOf("!"))
                            : innerUrlPath;
                    toReturn = new File(URLDecoder.decode(filePath, encoding));
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Could not get the File for [" + anURL + "]", e);
            }
        }

        // All done.
        return toReturn;
    }

    /**
     * Convenience method to successfully create a directory - or throw an exception if failing to create it.
     *
     * @param aDirectory        The directory to create.
     * @param cleanBeforeCreate if {@code true}, the directory and all its content will be deleted before being
     *                          re-created. This will ensure that the created directory is really clean.
     * @throws MojoExecutionException if the aDirectory could not be created (and/or cleaned).
     */
    public static void createDirectory(final File aDirectory, final boolean cleanBeforeCreate)
            throws MojoExecutionException {

        // Check sanity
        Validate.notNull(aDirectory, "aDirectory");
        validateFileOrDirectoryName(aDirectory);

        // Clean an existing directory?
        if (cleanBeforeCreate) {
            try {
                FileUtils.deleteDirectory(aDirectory);
            } catch (IOException e) {
                throw new MojoExecutionException("Could not clean directory [" + getCanonicalPath(aDirectory) + "]", e);
            }
        }

        // Now, make the required directory, if it does not already exist as a directory.
        final boolean existsAsFile = aDirectory.exists() && aDirectory.isFile();
        if (existsAsFile) {
            throw new MojoExecutionException("[" + getCanonicalPath(aDirectory) + "] exists and is a file. "
                    + "Cannot make directory");
        } else if (!aDirectory.exists() && !aDirectory.mkdirs()) {
            throw new MojoExecutionException("Could not create directory [" + getCanonicalPath(aDirectory) + "]");
        }
    }

    private static void validateFileOrDirectoryName(final File fileOrDir) {

        if (Os.isFamily(Os.FAMILY_WINDOWS) && !FileUtils.isValidWindowsFileName(fileOrDir)) {
            throw new IllegalArgumentException(
                    "The file (" + fileOrDir + ") cannot contain any of the following characters: \n"
                            + StringUtils.join(INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME, " "));
        }
    }
}
