from django.conf.urls import patterns, url


urlpatterns = patterns(
    '',

    # users
    url(r'^user/(\d+)/lists/?', 'sync-lists.api.users.views.get_user_lists', name='user lists'),
    url(r'^users/(\d+)/?', 'sync-lists.api.users.views.user_id', name='user id'),
    url(r'^users/login/?', 'sync-lists.api.users.views.login_user', name="login"),
    url(r'^users/?', 'sync-lists.api.users.views.create_user', name='create user'),

    # lists
    url(r'^lists/(\d+)/users/?', 'sync-lists.api.lists.views.get_list_users', name='list users'),
    url(r'^lists/(\d+)/tasks/?', 'sync-lists.api.lists.views.list_id_tasks', name='list tasks'),
    url(r'^lists/(\d+)/?', 'sync-lists.api.lists.views.list_id', name='list id'),
    url(r'^lists/?', 'sync-lists.api.lists.views.create_list', name='create list'),

    # tasks
    url(r'^tasks/(\d+)/?', 'sync-lists.api.tasks.views.task_id', name='task id'),

    url(r'^/?', 'sync-lists.api.views.index', name='index'),
)
