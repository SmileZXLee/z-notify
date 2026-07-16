<template>
  <page-header-wrapper>
    <a-card :bordered="false" style="margin-bottom: 10px">
      <a-row :gutter="10">
        <a-col :span="20" style="display: flex;align-items: center;">
          <strong style="margin-right: 10px">API文档地址:</strong>
          <a-input style="flex: 1;" :value="swaggerApiUrl" readonly></a-input>
        </a-col>
        <a-col :span="4">
          <a-button :span="4" type="primary" @click="onSwaggerApiUrl">在浏览器中打开</a-button>
        </a-col>
      </a-row>
    </a-card>
    <a-card :bordered="false">
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="8" :sm="24">
              <a-form-item label="项目名称">
                <a-input v-model="queryParam.keyword" placeholder="请输入项目名称"/>
              </a-form-item>
            </a-col>
            <a-col :md="16" :sm="16">
              <span class="table-page-search-submitButtons" style="display: flex;justify-content: flex-end;">
                <a-button type="primary" @click="$refs.table.refresh(true)">查询</a-button>
                <a-button style="margin-left: 8px" @click="() => this.queryParam = {}">重置</a-button>
              </span>
            </a-col>
          </a-row>
        </a-form>
      </div>

      <div class="table-operator">
        <a-button type="primary" icon="plus" @click="handleAdd">新建</a-button>
      </div>

      <s-table
        ref="table"
        size="default"
        rowKey="key"
        :columns="columns"
        :data="loadData"
        showPagination="auto"
      >
        <span slot="action" slot-scope="text, record">
          <template>
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm
              placement="topLeft"
              title="删除项目将同时删除项目下的所有关联数据，确定要删除吗？"
              ok-text="删除"
              cancel-text="取消"
              @confirm="handleDeleteProject(record)"
            >
              <a style="color:red">删除</a>
            </a-popconfirm>
          </template>
        </span>

        <a-button type="link" slot="notice_count" slot-scope="text,record" @click="handle2Page('notice', record)">
          {{ text || '添加' }}
        </a-button>
        <a-button type="link" slot="text_count" slot-scope="text,record" @click="handle2Page('text', record)">
          {{ text || '添加' }}
        </a-button>
        <a-button type="link" slot="version_count" slot-scope="text,record" @click="handle2Page('version', record)">
          {{ text || '添加' }}
        </a-button>
        <a-button type="link" slot="feedback_count" slot-scope="text,record" @click="handle2Page('feedback', record)">
          {{ text || '配置' }}
        </a-button>
        <a-button type="link" slot="statistics" slot-scope="text,record" @click="handle2Page('statistics', record)">
          {{ '前往' }}
        </a-button>
      </s-table>

      <create-form
        ref="createModal"
        :visible="visible"
        :loading="confirmLoading"
        :model="mdl"
        @cancel="handleCancel"
        @ok="handleOk"
      />
      <step-by-step-modal ref="modal" @ok="handleOk"/>
    </a-card>
  </page-header-wrapper>
</template>

<script>
import { STable, Ellipsis } from '@/components'
import { getProjectList, createProject, updateProject, deleteProject } from '@/api/project'

import StepByStepModal from './modules/StepByStepModal'
import CreateForm from './modules/CreateForm'

const columns = [
  {
    title: '项目名称',
    align: 'center',
    dataIndex: 'project_name'
  },
  {
    title: '通知数',
    dataIndex: 'notice_count',
    align: 'center',
    scopedSlots: { customRender: 'notice_count' }
  },
  {
    title: '文本数',
    dataIndex: 'text_count',
    align: 'center',
    scopedSlots: { customRender: 'text_count' }
  },
  {
    title: '版本数',
    dataIndex: 'version_count',
    align: 'center',
    scopedSlots: { customRender: 'version_count' }
  },
  {
    title: '反馈数',
    dataIndex: 'feedback_count',
    align: 'center',
    scopedSlots: { customRender: 'feedback_count' }
  },
  {
    title: '访问统计',
    dataIndex: 'statistics',
    align: 'center',
    scopedSlots: { customRender: 'statistics' }
  },
  {
    title: '操作',
    dataIndex: 'action',
    width: '150px',
    align: 'center',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'PorjectList',
  components: {
    STable,
    Ellipsis,
    CreateForm,
    StepByStepModal
  },
  data () {
    this.columns = columns
    return {
      // create model
      visible: false,
      confirmLoading: false,
      mdl: null,
      // 查询参数
      queryParam: {},
      // 加载数据方法 必须为 Promise 对象
      loadData: parameter => {
        const requestParameters = Object.assign({}, parameter, this.queryParam)
        console.log('loadData request parameters:', requestParameters)
        return getProjectList(requestParameters)
          .then(res => {
            return res.data
          })
      },
      selectedRowKeys: [],
      selectedRows: []
    }
  },
  computed: {
    swaggerApiUrl () {
      return `${process.env.VUE_APP_API_BASE_URL}/swagger-ui/index.html`
    }
  },
  methods: {
    handleAdd () {
      this.mdl = null
      this.visible = true
    },
    handleEdit (record) {
      this.visible = true
      this.mdl = { ...record }
    },
    handleOk () {
      const form = this.$refs.createModal.form
      this.confirmLoading = true
      form.validateFields((errors, values) => {
        if (!errors) {
          console.log('values', values)
          if (Array.isArray(values.platforms)) {
            values.platforms = values.platforms.join(',')
          }
          if (values.id) {
            // 更新
            updateProject(values).then(res => {
              this.visible = false
              // 重置表单数据
              form.resetFields()
              // 刷新表格
              this.$refs.table.refresh()

              this.$message.success('更新成功')
            })
            .finally(() => {
              this.confirmLoading = false
            })
          } else {
            // 新增
            createProject(values).then(res => {
              this.visible = false
              // 重置表单数据
              form.resetFields()
              // 刷新表格
              this.$refs.table.refresh()

              this.$message.success('创建成功')
            })
            .finally(() => {
              this.confirmLoading = false
            })
          }
        } else {
          this.confirmLoading = false
        }
      })
    },
    onSwaggerApiUrl () {
      window.open(this.swaggerApiUrl)
    },
    handleCancel () {
      this.visible = false

      const form = this.$refs.createModal.form
      form.resetFields() // 清理表单数据（可不做）
    },
    handleDeleteProject (record) {
      deleteProject(record.id).then(res => {
        this.$refs.table.refresh()
        this.$message.success('删除成功')
      })
    },
    handle2Page (page, record) {
      this.$router.push({ path: `/project/${page}-list`, query: { 'projectId': record.id, 'projectName': record.project_name } })
    }
  }
}
</script>
