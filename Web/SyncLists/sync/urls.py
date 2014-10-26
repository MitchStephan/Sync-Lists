from django.conf.urls import patterns, url


urlpatterns = patterns(
    '',

    # users
    url(r'^users/(\d+)/lists/?', 'sync.api.users.views.get_user_lists', name='user lists'),
    url(r'^users/(\d+)/?', 'sync.api.users.views.user_id', name='user id'),
    url(r'^users/login/?', 'sync.api.users.views.login_user', name="login"),
    url(r'^users/?', 'sync.api.users.views.create_user', name='create user'),

    # list tasks
    url(r'^lists/(\d+)/tasks/(\d+)/?', 'sync.api.tasks.views.task_id', name='task id'),
    url(r'^lists/(\d+)/tasks/?', 'sync.api.tasks.views.task', name='task'),

    # lists
    url(r'^lists/(\d+)/users/?', 'sync.api.lists.views.get_list_users', name='list users'),
    url(r'^lists/(\d+)/?', 'sync.api.lists.views.list_id', name='list id'),
    url(r'^lists/?', 'sync.api.lists.views.create_list', name='create list'),
)
