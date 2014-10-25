from django.conf.urls import patterns, url


urlpatterns = patterns(
    '',

    # users
    url(r'^user/(\d+)/lists/?', 'sl.api.users.views.get_user_lists', name='user lists'),
    url(r'^users/(\d+)/?', 'sl.api.users.views.user_id', name='user id'),
    url(r'^users/login/?', 'sl.api.users.views.login_user', name="login"),
    url(r'^users/?', 'sl.api.users.views.create_user', name='create user'),

    # lists
    url(r'^lists/(\d+)/users/?', 'sl.api.lists.views.get_list_users', name='list users'),
    url(r'^lists/(\d+)/tasks/?', 'sl.api.lists.views.list_id_tasks', name='list tasks'),
    url(r'^lists/(\d+)/?', 'sl.api.lists.views.list_id', name='list id'),
    url(r'^lists/?', 'sl.api.lists.views.create_list', name='create list'),

    # tasks
    url(r'^tasks/(\d+)/?', 'sl.api.tasks.views.task_id', name='task id'),

    url(r'^/?', 'sl.api.views.index', name='index'),
)
