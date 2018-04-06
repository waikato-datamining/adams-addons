from django.template import loader
from django.http import HttpResponse, HttpRequest


def create_error_response(request, title, error):
    """
    Generates an HttpResponse object with the specified title/error.
    :param request: the HttpRequest that triggered this error
    :type request: HttpRequest
    :param title: the title (' - Error' gets appended)
    :type title: str
    :param error: the error message
    :type error: str
    :return: the HttpResponse object
    :rtype: HttpResponse
    """

    template = loader.get_template('error.html')
    context = {
        'title': title + ' - Error',
        'error': error,
    }

    return HttpResponse(template.render(context, request))
