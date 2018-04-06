from django.shortcuts import redirect


def login_redirect(request):
    """
    Performs a redirect to the login page.

    :param request: the current request object
    :return: the response object
    """
    return redirect('/accounts/login/?next=%s' % request.path)