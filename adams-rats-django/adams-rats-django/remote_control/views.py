from django.http import HttpResponse, HttpResponseRedirect
from django.template import loader
from django.template.defaulttags import register
import logging
from adams_rats_django.shortcuts import login_redirect
import adams_rats_django.settings as settings
from adams_rats_django.settings import REMOTECONTROL_HOST
import adams_rats_django.applist as applist
from adams_rats_django.form_utils import get_variable, get_variable_with_error
from adams_rats_django.error import create_error_response
import requests
from requests.exceptions import ConnectionError
from adams_rats_django.settings import APPS_LONG

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


def select(request):
    if settings.LOGIN_REQUIRED and not request.user.is_authenticated:
        return login_redirect(request)
    template = loader.get_template('remote_control/select.html')
    try:
        response = requests.get(REMOTECONTROL_HOST + "rats/control/flows")
    except ConnectionError:
        return create_error_response(request, APPS_LONG["remote_control"], "ADAMS not running?")
    jobj = response.json()
    flows = jobj["flows"]
    if len(flows) == 1:
        flow = flows[0]
        return HttpResponseRedirect("/remote_control/control?id=" + str(flow["id"]) + "&showbackbutton=false")
    elif len(flows) == 0:
        return create_error_response(request, APPS_LONG["remote_control"], "No running flows found!")

    context = applist.template_context('remote_control')
    context['flows'] = flows
    context['subtitle'] = 'Select flow'
    return HttpResponse(template.render(context, request))


def control(request):
    if settings.LOGIN_REQUIRED and not request.user.is_authenticated:
        return login_redirect(request)
    template = loader.get_template('remote_control/control.html')
    error, id = get_variable_with_error(request, 'Remote control', 'id')
    showbackbutton = get_variable(request, 'showbackbutton', def_value="true") == "true"
    if error is not None:
        return error

    try:
        response = requests.get(REMOTECONTROL_HOST + "rats/control/status/" + id)
    except ConnectionError:
        return create_error_response(request, APPS_LONG["remote_control"], "ADAMS not running?")
    jobj = response.json()
    if "groups" not in jobj:
        return create_error_response(request, APPS_LONG["remote_control"], "Nothing to control!")
    groups = jobj["groups"]
    context = applist.template_context('remote_control')
    context['showbackbutton'] = showbackbutton
    context['groups'] = groups
    context['id'] = id
    return HttpResponse(template.render(context, request))


def command(request):
    if settings.LOGIN_REQUIRED and not request.user.is_authenticated:
        return login_redirect(request)
    error, id = get_variable_with_error(request, 'Remote control', 'id')
    if error is not None:
        return error
    error, rat = get_variable_with_error(request, 'Remote control', 'rat')
    if error is not None:
        return error
    error, command = get_variable_with_error(request, 'Remote control', 'command')
    if error is not None:
        return error
    showbackbutton = get_variable(request, 'showbackbutton', def_value="true") == "true"
    try:
        requests.get(REMOTECONTROL_HOST + "rats/control/command/" + id + "/" + command + "?rat=" + rat)
    except ConnectionError:
        return create_error_response(request, APPS_LONG["remote_control"], "ADAMS not running?")
    return HttpResponseRedirect("/remote_control/control?id=" + str(id) + "&showbackbutton=" + str(showbackbutton))
