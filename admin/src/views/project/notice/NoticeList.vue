<template>
  <page-header-wrapper :title="`${projectName}-${pageTitle}`">
    <a-card :bordered="false" style="margin-bottom: 10px">
      <a-row :gutter="10">
        <a-col :span="20" style="display: flex;align-items: center;">
          <strong style="margin-right: 10px">通知API:</strong>
          <a-input style="flex: 1;" :value="noticeApiUrl" readonly></a-input>
        </a-col>
        <a-col :span="4">
          <a-button :span="4" type="primary" v-clipboard:copy="noticeApiUrl" v-clipboard:success="onCopySuccess">复制到剪贴板</a-button>
        </a-col>
      </a-row>
    </a-card>
    <a-card :bordered="false">
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
              title="确定要删除这条通知吗？"
              ok-text="删除"
              cancel-text="取消"
              @confirm="handleDeleteNotice(record)"
            >
              <a style="color:red">删除</a>
            </a-popconfirm>
          </template>
        </span>

        <span slot="content" slot-scope="text">
          <ellipsis :length="15" tooltip>{{ text }}</ellipsis>
        </span>

        <span slot="status" slot-scope="status">
          <a-badge :status="status ? 'success' : 'error'" :text="status ? '有效' : '已过期'" />
        </span>
      </s-table>

      <create-notice
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
import moment from 'moment'
import { STable, Ellipsis } from '@/components'
import { getNoticeList, createNotice, updateNotice, deleteNotice } from '@/api/notice'

import StepByStepModal from '../modules/StepByStepModal'
import CreateNotice from '../modules/CreateNotice'

const columns = [
  {
    title: '通知标题',
    dataIndex: 'title'
  },
  {
    title: '通知内容',
    dataIndex: 'content',
    scopedSlots: { customRender: 'content' }
  },
  {
    title: '过期时间',
    dataIndex: 'expiretime',
    customRender: expiretime => expiretime || '永不过期'
  },
  {
    title: '状态',
    dataIndex: 'status',
    scopedSlots: { customRender: 'status' }
  },
  {
    title: '操作',
    dataIndex: 'action',
    width: '150px',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'NoticeList',
  components: {
    STable,
    Ellipsis,
    CreateNotice,
    StepByStepModal
  },
  data () {
    this.columns = columns
    return {
      projectId: this.$route.query.projectId,
      projectName: this.$route.query.projectName,
      pageTitle: this.$route.meta.title,
      // create model
      visible: false,
      confirmLoading: false,
      mdl: null,
      // 高级搜索 展开/关闭
      advanced: false,
      // 查询参数
      queryParam: {},
      // 加载数据方法 必须为 Promise 对象
      loadData: parameter => {
        const requestParameters = Object.assign({}, parameter, this.queryParam)
        console.log('loadData request parameters:', requestParameters)
        return getNoticeList(this.projectId, requestParameters)
          .then(res => {
            return res.data
          })
      },
      selectedRowKeys: [],
      selectedRows: []
    }
  },
  computed: {
    noticeApiUrl () {
      return this.projectId ? `${process.env.VUE_APP_API_BASE_URL}/v1/public/notices/${this.projectId}` : '无'
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
          values.project_id = this.projectId
          if (values.id) {
            // 新增
            updateNotice(values).then(res => {
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
            createNotice(values).then(res => {
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
    handleCancel () {
      this.visible = false

      const form = this.$refs.createModal.form
      form.resetFields() // 清理表单数据（可不做）
    },
    handleDeleteNotice (record) {
      deleteNotice(record.id).then(res => {
        this.$refs.table.refresh()
        this.$message.success('删除成功')
      })
    },
    onSelectChange (selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys
      this.selectedRows = selectedRows
    },
    onCopySuccess () {
      this.$message.success('已复制到剪贴板')
    },
    toggleAdvanced () {
      this.advanced = !this.advanced
    },
    resetSearchForm () {
      this.queryParam = {
        date: moment(new Date())
      }
    }
  }
}
</script>
