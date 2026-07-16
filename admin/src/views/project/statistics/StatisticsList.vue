<template>
  <page-header-wrapper :title="`${projectName}-${pageTitle}`">
    <a-card :bordered="false" style="margin-bottom: 10px">
      <a-row :gutter="10">
        <a-col :span="20" style="display: flex;align-items: center;">
          <strong style="margin-right: 10px">上传统计API:</strong>
          <a-input style="flex: 1;" :value="statisticsApiUrl" readonly></a-input>
        </a-col>
        <a-col :span="4">
          <a-button :span="4" type="primary" v-clipboard:copy="statisticsApiUrl" v-clipboard:success="onCopySuccess">复制到剪贴板</a-button>
        </a-col>
      </a-row>
      <a-row :gutter="10" style="margin-top: 10px">
        <a-col :span="20" style="display: flex;align-items: center;">
          <strong style="margin-right: 10px">上传并展示Badge:</strong>
          <a-input style="flex: 1;" :value="statisticsAndBadgeApiUrl" readonly></a-input>
        </a-col>
        <a-col :span="4">
          <a-button :span="4" type="primary" v-clipboard:copy="statisticsAndBadgeApiUrl" v-clipboard:success="onCopySuccess">复制到剪贴板</a-button>
        </a-col>
      </a-row>
    </a-card>
    <a-card :bordered="false">
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="8" :sm="8">
              <a-tag color="blue" style="font-size: 13px;font-weight: bold">
                访问次数：{{ total }}
              </a-tag>
            </a-col>
            <a-col :md="16" :sm="16">
              <span class="table-page-search-submitButtons" style="display: flex;justify-content: flex-end;">
                <a-button type="primary" icon="bar-chart" @click="handle2Analysis" style="margin-right: 15px">查看统计分析</a-button>
                <a-button type="primary" icon="sync" @click="$refs.table.refresh(true)">刷新</a-button>
              </span>
            </a-col>
          </a-row>
        </a-form>
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
              title="确定要删除吗？"
              ok-text="删除"
              cancel-text="取消"
              @confirm="handleDelete(record)"
            >
              <a style="color:red">删除</a>
            </a-popconfirm>
          </template>
        </span>
        <span slot="tag" slot-scope="text">
          <ellipsis :length="30" tooltip>{{ text || '-' }}</ellipsis>
        </span>
        <span slot="from" slot-scope="text">
          <ellipsis :length="30" tooltip>{{ text || '-' }}</ellipsis>
        </span>
      </s-table>

      <create-version
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
import { getStatisticsList } from '@/api/statistics'

import StepByStepModal from '../modules/StepByStepModal'
import CreateVersion from '../modules/CreateVersion'

const columns = [
  {
    title: 'ip',
    align: 'center',
    dataIndex: 'ip'
  },
  {
    title: 'ip归属地',
    align: 'center',
    dataIndex: 'ip_region'
  },
  {
    title: '来源',
    align: 'center',
    dataIndex: 'from',
    scopedSlots: { customRender: 'from' }
  },
  {
    title: '标签',
    align: 'center',
    dataIndex: 'tag',
    scopedSlots: { customRender: 'tag' }
  },
  {
    title: '访问时间',
    align: 'center',
    dataIndex: 'createtime'
  }
]

export default {
  name: 'NoticeList',
  components: {
    STable,
    Ellipsis,
    CreateVersion,
    StepByStepModal
  },
  data () {
    this.columns = columns
    return {
      projectId: this.$route.query.projectId,
      projectName: this.$route.query.projectName,
      pageTitle: this.$route.meta.title,
      total: 0,
      // create model
      visible: false,
      confirmLoading: false,
      mdl: null,
      currentDowenloadUrl: '',
      // 查询参数
      queryParam: {},
      // 加载数据方法 必须为 Promise 对象
      loadData: parameter => {
        const requestParameters = Object.assign({}, parameter, this.queryParam)
        console.log('loadData request parameters:', requestParameters)
        return getStatisticsList(this.projectId, requestParameters)
          .then(res => {
            if (res.data.results.length) {
              this.currentDowenloadUrl = res.data.results[0].download_url
            }
            this.total = res.data.total
            return res.data
          })
      },
      selectedRowKeys: [],
      selectedRows: []
    }
  },
  computed: {
    statisticsApiUrl () {
      return this.projectId ? `${process.env.VUE_APP_API_BASE_URL}/v1/public/statistics/${this.projectId}` : '无'
    },
    statisticsAndBadgeApiUrl () {
      return this.projectId ? `${process.env.VUE_APP_API_BASE_URL}/v1/public/statistics/${this.projectId}/badge` : '无'
    }
  },
  methods: {
    handle2Analysis () {
      this.$router.push({ path: `/project/statistics-list/analysis`, query: { 'projectId': this.projectId, 'projectName': this.projectName } })
    },
    onSelectChange (selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys
      this.selectedRows = selectedRows
    },
    onCopySuccess () {
      this.$message.success('已复制到剪贴板')
    }
  }
}
</script>
