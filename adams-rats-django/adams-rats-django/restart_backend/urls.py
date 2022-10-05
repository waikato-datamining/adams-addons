from django.urls import re_path

from . import views

urlpatterns = [
    re_path(r'^$', views.restart, name='restart'),
    re_path(r'^results$', views.output, name='output'),
]
