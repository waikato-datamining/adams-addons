"""contactcollector URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/4.1/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  re_path(r'^$', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  re_path(r'^$', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import re_path, include
    2. Add a URL to urlpatterns:  re_path(r'^blog/', include('blog.urls'))
"""
from django.urls import re_path, include
from django.contrib import admin

from . import views

urlpatterns = [
    re_path(r'^$', views.index, name='index'),
    re_path(r'^remote_control/', include(('remote_control.urls', 'adams_rats_django'), namespace="remote_control")),
    re_path(r'^restart_backend/', include(('restart_backend.urls', 'adams_rats_django'), namespace="restart_backend")),
    re_path(r'^admin/', admin.site.urls),
    re_path(r'^accounts/', include('django.contrib.auth.urls')),
]

