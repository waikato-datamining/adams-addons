from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^$', views.restart, name='restart'),
    url(r'^results$', views.output, name='output'),
]
