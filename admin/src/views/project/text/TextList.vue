<template>
  <page-header-wrapper :title="`${projectName}-${pageTitle}`">
    <a-card :bordered="false">
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="8" :sm="24">
              <a-form-item label="key">
                <a-input v-model="queryParam.keyword" placeholder="请输入key"/>
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
              title="确定要删除这条文本吗？"
              ok-text="删除"
              cancel-text="取消"
              @confirm="handleDelete(record)"
            >
              <a style="color:red">删除</a>
            </a-popconfirm>
          </template>
        </span>
        <span slot="value" slot-scope="text">
          <ellipsis :length="15" tooltip>{{ text }}</ellipsis>
        </span>
        <span slot="api_address" slot-scope="text, record">
          <template>
            <a v-clipboard:copy="getApiAddress(record)" v-clipboard:success="onCopySuccess">点击复制</a>
          </template>
        </span>
      </s-table>

      <create-text
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
import { getTextList, createText, updateText, deleteText } from '@/api/text'

import StepByStepModal from '../modules/StepByStepModal'
import CreateText from '../modules/CreateText'

const columns = [
  {
    title: '文本key',
    align: 'center',
    dataIndex: 'key'
  },
  {
    title: '文本value',
    dataIndex: 'value',
    align: 'center',
    scopedSlots: { customRender: 'value' }
  },
  {
    title: '创建时间',
    align: 'center',
    dataIndex: 'createtime'
  },
  {
    title: 'API地址',
    dataIndex: 'api_address',
    align: 'center',
    scopedSlots: { customRender: 'api_address' }
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
  name: 'NoticeList',
  components: {
    STable,
    Ellipsis,
    CreateText,
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
      // 查询参数
      queryParam: {},
      // 加载数据方法 必须为 Promise 对象
      loadData: parameter => {
        const requestParameters = Object.assign({}, parameter, this.queryParam)
        console.log('loadData request parameters:', requestParameters)
        return getTextList(this.projectId, requestParameters)
          .then(res => {
            return res.data
          })
      },
      selectedRowKeys: [],
      selectedRows: []
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
            updateText(values).then(res => {
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
            createText(values).then(res => {
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
    handleDelete (record) {
      deleteText(record.id).then(res => {
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
    getApiAddress (record) {
      return record.key ? `${process.env.VUE_APP_API_BASE_URL}/v1/public/text/${this.projectId}/${record.key}` : '无'
    }
  }
}
</script>
