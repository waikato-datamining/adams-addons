from django.urls import re_path

from . import views

urlpatterns = [
    re_path(r'^$', views.select, name='select'),
    re_path(r'^control$', views.control, name='control'),
    re_path(r'^command$', views.command, name='command'),
]
