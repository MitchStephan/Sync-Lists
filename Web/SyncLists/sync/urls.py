from django.conf.urls import patterns, url


urlpatterns = patterns(
    '',

    # users
    url(r'^user/lists/?', 'sync.api.users.views.get_user_lists', name='user lists'),
    url(r'^user/login/?', 'sync.api.users.views.login_user', name="login"),
    url(r'^user/?', 'sync.api.users.views.user', name='user'),

    # list tasks
    url(r'^lists/(\d+)/tasks/(\d+)/?', 'sync.api.tasks.views.task_id', name='task id'),
    url(r'^lists/(\d+)/tasks/?', 'sync.api.tasks.views.task', name='task'),

    # lists
    url(r'^lists/(\d+)/users/(\d+)/?', 'sync.api.lists.views.list_users_id', name='list users id'),
    url(r'^lists/(\d+)/users/?', 'sync.api.lists.views.get_list_users', name='list users'),
    url(r'^lists/(\d+)/?', 'sync.api.lists.views.list_id', name='list id'),
    url(r'^lists/?', 'sync.api.lists.views.create_list', name='create list'),
)
