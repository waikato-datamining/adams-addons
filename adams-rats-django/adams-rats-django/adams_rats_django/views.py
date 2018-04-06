from django.http import HttpResponse
from django.template import loader
import logging
from adams_rats_django.shortcuts import login_redirect
import adams_rats_django.settings as settings
import adams_rats_django.applist as applist

logger = logging.getLogger(__name__)


def index(request):
    """
    Displays the "home" page.

    :param request: the request
    :return: the response
    """
    if settings.LOGIN_REQUIRED and not request.user.is_authenticated:
        return login_redirect(request)
    template = loader.get_template('home.html')
    context = applist.template_context()
    return HttpResponse(template.render(context, request))
