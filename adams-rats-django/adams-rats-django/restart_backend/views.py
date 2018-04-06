from django.http import HttpResponse
from django.template import loader
from django.template.defaulttags import register
import logging
from adams_rats_django.shortcuts import login_redirect
import adams_rats_django.settings as settings
import adams_rats_django.applist as applist
import subprocess

logger = logging.getLogger(__name__)


@register.filter
def get_item(dictionary, key):
    """
    Filter method for retrieving the value of a dictionary.

    Taken from here:
    http://stackoverflow.com/a/8000091

    {{ mydict|get_item:item.NAME }}
    """
    return dictionary.get(key)


def restart(request):
    if settings.LOGIN_REQUIRED and not request.user.is_authenticated:
        return login_redirect(request)
    template = loader.get_template('restart_backend/restart.html')
    context = applist.template_context('restart_backend')
    context['can_restart'] = settings.RESTART_COMMAND is not None
    return HttpResponse(template.render(context, request))


def output(request):
    if settings.LOGIN_REQUIRED and not request.user.is_authenticated:
        return login_redirect(request)
    template = loader.get_template('restart_backend/output.html')
    context = applist.template_context('restart_backend')
    try:
        completed = subprocess.run(settings.RESTART_COMMAND, stdout=subprocess.PIPE)
        output = completed.stdout.decode().replace("\n", "<br/>")
    except Exception as e:
        completed = None
        output = str(e).replace("\n", "<br/>")
    context['success'] = (completed is not None) and (completed.returncode == 0)
    context['output'] = output
    return HttpResponse(template.render(context, request))
