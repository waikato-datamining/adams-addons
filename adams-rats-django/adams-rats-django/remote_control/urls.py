from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^$', views.select, name='select'),
    url(r'^control$', views.control, name='control'),
    url(r'^command$', views.command, name='command'),
]
