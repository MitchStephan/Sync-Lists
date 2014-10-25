from django.conf.urls import patterns, url


urlpatterns = patterns(
    '',
    # users
    url(r'^users/?', 'sl.api.users.views.users', name='user'),
    url(r'^users/(\d+)/?', 'sl.api.users.views.user_id', name='user id'),
    url(r'^users/login/?', 'sl.api.users.views.login_user', name="login"),

    # lists
    url(r'^lists/?', 'sl.api.lists.views.index', name='index'),
    # url(r'^lists/(\d+)/?', 'sl.api.lists.views.list_id', name='user id'),

    # tasks
    url(r'^tasks/?', 'sl.api.tasks.views.index', name='index'),

    url(r'^/?', 'sl.api.views.index', name='index'),
)
