from adams_rats_django.settings import APPS_LIST
from adams_rats_django.settings import APPS_SHORT
from adams_rats_django.settings import APPS_LONG
from adams_rats_django.settings import APPS_TYPE
from adams_rats_django.settings import APPS_TYPE_NAME
from adams_rats_django.settings import APPS_PRODUCTION
from adams_rats_django.settings import PRODUCTION
from adams_rats_django.settings import LOGIN_REQUIRED


def get_apps():
    """
    Returns a list of all apps in the project.

    :return: list of app names
    :rtype: list
    """

    if not PRODUCTION:
        return APPS_LIST

    result = []
    for app in APPS_LIST:
        if APPS_PRODUCTION[app]:
            result.append(app)
    return result


def get_appname_short(name):
    """
    Returns the short name for the app.

    :param name: the name of the app
    :type name: str
    :return: the short name
    :rtype: str
    """

    return APPS_SHORT[name]


def get_appname_long(name):
    """
    Returns the long name for the app.

    :param name: the name of the app
    :type name: str
    :return: the long name
    :rtype: str
    """

    return APPS_LONG[name]


def template_context(app=None):
    """
    Returns the context for a template.

    :param app: the current active app
    :type app: str
    :return: the context
    :rtype: dict
    """

    result = {
        'apps': get_apps(),
        'apps_short': APPS_SHORT,
        'apps_long': APPS_LONG,
        'apps_type': APPS_TYPE,
        'apps_type_name': APPS_TYPE_NAME,
        'login_required': LOGIN_REQUIRED
    }

    if app is not None:
        result['title'] = APPS_LONG[app]
        result['app'] = app
    else:
        result['title'] = 'Home'

    return result
