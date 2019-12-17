/*
 * Groovy transformer for querying NZ public holidays.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */


import adams.flow.core.Token
import adams.flow.transformer.AbstractScript
import wslite.soap.SOAPClient

class TemplateTransformer
  extends AbstractScript {

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Groovy transformer for querying NZ public holidays."
  }

  /**
   * Returns the class of objects that it accepts.
   *
   * @return		the accepted classes
   */
  public Class[] accepts() {
    return [String.class] as Object[]
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the generated classes
   */
  public Class[] generates() {
    return [String.class] as Object[]
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String doExecute() {
    def client = new SOAPClient('https://kayaposoft.com/enrico/ws/v2.0/index.php')
    def response = client.send(SOAPAction:'https://kayaposoft.com/enrico/ws/v2.0/index.php') {
      body {
        getHolidaysForYear('xmlns':'http://www.kayaposoft.com/enrico/ws/v2.0/') {
          year(m_InputToken.getPayload().toString())  // the year
          country("nz")  // New Zealand
          region("wko")  // Waikato
          holidayType("public_holiday")  // the type
        }
      }
    }
    def days = []
    response.getHolidaysForYearResponse.holidays.holiday.each{
      def day = [it.name.text, " on ", it.date.day, "/", it.date.month, "/", it.date.year]
      days.add(day.join(""))
    }
    m_OutputToken = new Token(days.join("\n"))
    return null
  }
}
