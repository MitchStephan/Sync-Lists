# -*- coding: utf-8 -*-
from south.utils import datetime_utils as datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding model 'User'
        db.create_table(u'sync_user', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('email', self.gf('django.db.models.fields.CharField')(max_length=255)),
            ('password', self.gf('django.db.models.fields.CharField')(max_length=255)),
            ('date_created', self.gf('django.db.models.fields.DateTimeField')(auto_now_add=True, blank=True)),
        ))
        db.send_create_signal(u'sync', ['User'])

        # Adding model 'List'
        db.create_table(u'sync_list', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=225)),
            ('list_owner', self.gf('django.db.models.fields.related.ForeignKey')(related_name='list owner', to=orm['sync.User'])),
            ('date_created', self.gf('django.db.models.fields.DateTimeField')(auto_now_add=True, blank=True)),
        ))
        db.send_create_signal(u'sync', ['List'])

        # Adding M2M table for field shared_users on 'List'
        m2m_table_name = db.shorten_name(u'sync_list_shared_users')
        db.create_table(m2m_table_name, (
            ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True)),
            ('list', models.ForeignKey(orm[u'sync.list'], null=False)),
            ('user', models.ForeignKey(orm[u'sync.user'], null=False))
        ))
        db.create_unique(m2m_table_name, ['list_id', 'user_id'])

        # Adding model 'Task'
        db.create_table(u'sync_task', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=225)),
            ('list', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['sync.List'])),
            ('completed', self.gf('django.db.models.fields.BooleanField')()),
            ('visible', self.gf('django.db.models.fields.BooleanField')()),
            ('task_owner', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['sync.User'])),
            ('date_created', self.gf('django.db.models.fields.DateTimeField')(auto_now_add=True, blank=True)),
        ))
        db.send_create_signal(u'sync', ['Task'])


    def backwards(self, orm):
        # Deleting model 'User'
        db.delete_table(u'sync_user')

        # Deleting model 'List'
        db.delete_table(u'sync_list')

        # Removing M2M table for field shared_users on 'List'
        db.delete_table(db.shorten_name(u'sync_list_shared_users'))

        # Deleting model 'Task'
        db.delete_table(u'sync_task')


    models = {
        u'sync.list': {
            'Meta': {'object_name': 'List'},
            'date_created': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'list_owner': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'list owner'", 'to': u"orm['sync.User']"}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '225'}),
            'shared_users': ('django.db.models.fields.related.ManyToManyField', [], {'related_name': "'shared users'", 'symmetrical': 'False', 'to': u"orm['sync.User']"})
        },
        u'sync.task': {
            'Meta': {'object_name': 'Task'},
            'completed': ('django.db.models.fields.BooleanField', [], {}),
            'date_created': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'list': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['sync.List']"}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '225'}),
            'task_owner': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['sync.User']"}),
            'visible': ('django.db.models.fields.BooleanField', [], {})
        },
        u'sync.user': {
            'Meta': {'object_name': 'User'},
            'date_created': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            'email': ('django.db.models.fields.CharField', [], {'max_length': '255'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'password': ('django.db.models.fields.CharField', [], {'max_length': '255'})
        }
    }

    complete_apps = ['sync']