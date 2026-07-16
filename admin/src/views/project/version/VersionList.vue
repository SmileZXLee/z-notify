<template>
  <page-header-wrapper :title="`${projectName}-${pageTitle}`">
    <a-card :bordered="false" style="margin-bottom: 12px;" :body-style="{ padding: '16px 24px' }">
      <div style="display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 8px;">
        <span style="font-weight: 600; font-size: 14px; color: rgba(0, 0, 0, 0.85); display: flex; align-items: center;">
          <a-icon type="api" style="margin-right: 8px; color: #1890ff; font-size: 16px;" />
          开发接入 (API)
        </span>
        <a-radio-group v-model="selectedApiType" button-style="solid" size="small">
          <a-radio-button value="standard">标准接口</a-radio-button>
          <a-radio-button value="tauri">Tauri 更新器</a-radio-button>
          <a-radio-button value="electron">Electron 更新器</a-radio-button>
        </a-radio-group>
      </div>

      <div style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; padding: 10px 16px; margin-top: 12px; display: flex; align-items: center; justify-content: space-between;">
        <div style="display: flex; align-items: center; flex: 1; overflow: hidden; margin-right: 12px;">
          <span style="background: #f0fdf4; color: #16a34a; border: 1px solid #bbf7d0; font-weight: 700; font-size: 10px; padding: 1px 6px; border-radius: 4px; margin-right: 10px; flex-shrink: 0; letter-spacing: 0.5px;">GET</span>
          <span style="font-family: SFMono-Regular, Consolas, Liberation Mono, Menlo, monospace; font-size: 13px; color: #334155; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; flex: 1; user-select: all;">
            {{ versionApiUrl }}
          </span>
        </div>
        <a-tooltip title="复制接口地址">
          <a-button
            type="default"
            size="small"
            icon="copy"
            v-clipboard:copy="versionApiUrl"
            v-clipboard:success="onCopySuccess"
            style="color: #64748b; background: #ffffff; border-color: #cbd5e1; border-radius: 4px;"
          >
            复制
          </a-button>
        </a-tooltip>
      </div>
    </a-card>
    <a-card :bordered="false">
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="8" :sm="24">
              <a-form-item label="版本号">
                <a-input v-model="queryParam.keyword" placeholder="请输入版本号"/>
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
              title="确定要删除这个版本吗？"
              ok-text="删除"
              cancel-text="取消"
              @confirm="handleDelete(record)"
            >
              <a style="color:red">删除</a>
            </a-popconfirm>
          </template>
        </span>
        <span slot="content" slot-scope="text, record">
          <div style="text-align: center; display: flex; align-items: center; justify-content: center;">
            <ellipsis :length="30" tooltip>{{ formatChangelog(text) }}</ellipsis>
            <a-popover v-if="record.platform_settings && Object.keys(record.platform_settings).length > 0" title="多平台个性化更新内容" placement="top">
              <template slot="content">
                <div style="padding: 4px 0; max-width: 400px;">
                  <div v-for="(override, platform) in record.platform_settings" :key="platform" style="margin-bottom: 6px; display: flex; align-items: flex-start;">
                    <a-tag size="small" :color="override.enable === false ? 'red' : 'blue'" style="font-size: 10px; padding: 0 4px; height: 16px; line-height: 14px; margin-right: 8px; min-width: 80px; text-align: center; margin-top: 1px;">
                      {{ platform }}
                    </a-tag>
                    <span style="font-size: 11px; word-break: break-all; white-space: normal; display: inline-block; max-width: 280px;">
                      <span v-if="override.enable === false" style="color: #f5222d; font-weight: 500;">已禁用此版本</span>
                      <span v-else-if="override.content" style="color: #333;">{{ formatChangelog(override.content) }}</span>
                      <span v-else style="color: #999;">继承全局</span>
                    </span>
                  </div>
                </div>
              </template>
              <a-tag color="blue" style="font-size: 10px; margin-left: 6px; cursor: pointer; padding: 0 4px; height: 16px; line-height: 14px;">多平台</a-tag>
            </a-popover>
          </div>
        </span>
        <span slot="download_url" slot-scope="text, record">
          <div style="text-align: center; display: flex; align-items: center; justify-content: center;">
            <ellipsis :length="30" tooltip>{{ text || '-' }}</ellipsis>
            <a-popover v-if="record.platform_settings && Object.keys(record.platform_settings).length > 0" title="多平台下载地址详情" placement="top">
              <template slot="content">
                <div style="padding: 4px 0; max-width: 450px;">
                  <div v-for="(override, platform) in record.platform_settings" :key="platform" style="margin-bottom: 6px; display: flex; align-items: flex-start;">
                    <a-tag size="small" :color="override.enable === false ? 'red' : 'blue'" style="font-size: 10px; padding: 0 4px; height: 16px; line-height: 14px; margin-right: 8px; min-width: 80px; text-align: center; margin-top: 1px;">
                      {{ platform }}
                    </a-tag>
                    <span style="font-size: 11px; word-break: break-all; white-space: normal; display: inline-block; max-width: 320px;">
                      <span v-if="override.enable === false" style="color: #f5222d; font-weight: 500;">已禁用此版本</span>
                      <span v-else-if="override.download_url" style="color: #333;">{{ override.download_url }}</span>
                      <span v-else style="color: #999;">继承全局</span>
                    </span>
                  </div>
                </div>
              </template>
              <a-tag color="blue" style="font-size: 10px; margin-left: 6px; cursor: pointer; padding: 0 4px; height: 16px; line-height: 14px;">多平台</a-tag>
            </a-popover>
          </div>
        </span>
        <span slot="config" slot-scope="text, record">
          <div style="text-align: center; display: flex; align-items: center; justify-content: center;">
            <ellipsis :length="30" tooltip>{{ text || '-' }}</ellipsis>
            <a-popover v-if="record.platform_settings && Object.keys(record.platform_settings).length > 0" title="多平台个性化配置内容" placement="top">
              <template slot="content">
                <div style="padding: 4px 0; max-width: 400px;">
                  <div v-for="(override, platform) in record.platform_settings" :key="platform" style="margin-bottom: 6px; display: flex; align-items: flex-start;">
                    <a-tag size="small" :color="override.enable === false ? 'red' : 'blue'" style="font-size: 10px; padding: 0 4px; height: 16px; line-height: 14px; margin-right: 8px; min-width: 80px; text-align: center; margin-top: 1px;">
                      {{ platform }}
                    </a-tag>
                    <span style="font-size: 11px; word-break: break-all; white-space: normal; display: inline-block; max-width: 280px;">
                      <span v-if="override.enable === false" style="color: #f5222d; font-weight: 500;">已禁用此版本</span>
                      <span v-else-if="override.config" style="color: #333;">{{ override.config }}</span>
                      <span v-else style="color: #999;">继承全局</span>
                    </span>
                  </div>
                </div>
              </template>
              <a-tag color="blue" style="font-size: 10px; margin-left: 6px; cursor: pointer; padding: 0 4px; height: 16px; line-height: 14px;">多平台</a-tag>
            </a-popover>
          </div>
        </span>
      </s-table>

      <create-version
        ref="createModal"
        :visible="visible"
        :loading="confirmLoading"
        :model="mdl"
        :platforms="projectPlatforms"
        @cancel="handleCancel"
        @ok="handleOk"
      />
      <step-by-step-modal ref="modal" @ok="handleOk"/>
    </a-card>
  </page-header-wrapper>
</template>

<script>
import { STable, Ellipsis } from '@/components'
import { getVersionList, createVersion, updateVersion, deleteVersion } from '@/api/version'
import { getProject } from '@/api/project'

import StepByStepModal from '../modules/StepByStepModal'
import CreateVersion from '../modules/CreateVersion'

const columns = [
  {
    title: '版本号',
    align: 'center',
    dataIndex: 'version'
  },
  {
    title: '更新内容',
    align: 'center',
    dataIndex: 'content',
    scopedSlots: { customRender: 'content' }
  },
  {
    title: '配置内容',
    align: 'center',
    dataIndex: 'config',
    scopedSlots: { customRender: 'config' }
  },
  {
    title: '下载地址',
    align: 'center',
    dataIndex: 'download_url',
    scopedSlots: { customRender: 'download_url' }
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
    CreateVersion,
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
      currentDowenloadUrl: '',
      latestVersionRecord: null,
      projectPlatforms: [],
      selectedApiType: 'standard',
      // 查询参数
      queryParam: {},
      // 加载数据方法 必须为 Promise 对象
      loadData: parameter => {
        const requestParameters = Object.assign({}, parameter, this.queryParam)
        console.log('loadData request parameters:', requestParameters)
        return getVersionList(this.projectId, requestParameters)
          .then(res => {
            if (res.data.results.length) {
              this.currentDowenloadUrl = res.data.results[0].download_url
              this.latestVersionRecord = res.data.results[0]
            }
            return res.data
          })
      },
      selectedRowKeys: [],
      selectedRows: []
    }
  },
  created () {
    this.loadProjectDetails()
  },
  computed: {
    versionApiUrl () {
      if (!this.projectId) return '无'
      const base = `${process.env.VUE_APP_API_BASE_URL}/v1/public/versions/${this.projectId}/当前版本号`
      if (this.selectedApiType === 'tauri') {
        return `${base}/tauri?platform={platform}&lang={lang}`
      } else if (this.selectedApiType === 'electron') {
        return `${base}/electron?platform={platform}&lang={lang}`
      } else {
        return `${base}?platform={platform}&lang={lang}`
      }
    }
  },
  methods: {
    handleAdd () {
      console.log(this.$route.params)
      const newModel = {
        download_url: this.currentDowenloadUrl,
        config: '',
        content: '',
        platform_settings: {}
      }

      if (this.latestVersionRecord) {
        const latest = this.latestVersionRecord
        const downloadUrl = latest.downloadUrl !== undefined ? latest.downloadUrl : latest.download_url
        const config = latest.config || ''
        const platformSettings = latest.platformSettings !== undefined ? latest.platformSettings : latest.platform_settings

        newModel.download_url = typeof downloadUrl === 'string' ? downloadUrl : ''
        newModel.config = config

        if (platformSettings) {
          const newPlatformSettings = {}
          const oldSettings = platformSettings

          Object.keys(oldSettings).forEach(platform => {
            const platformConfig = oldSettings[platform] || {}
            newPlatformSettings[platform] = {
              enable: platformConfig.enable !== undefined ? platformConfig.enable : true,
              download_url: platformConfig.download_url !== undefined ? platformConfig.download_url : (platformConfig.downloadUrl || ''),
              config: platformConfig.config || '',
              content: '' // do NOT fill changelog
            }
          })
          newModel.platform_settings = newPlatformSettings
        }
      }

      this.mdl = newModel
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

          // 封装默认更新内容的多个语言字段到 JSON 字符串中
          const contentMap = {
            'zh-Hans': values.content_zh_Hans || '',
            'zh-Hant': values.content_zh_Hant || '',
            'en': values.content_en || ''
          }
          values.content = JSON.stringify(contentMap)
          delete values.content_zh_Hans
          delete values.content_zh_Hant
          delete values.content_en

          // 封装各平台的独立覆盖参数到 platform_settings
          const platformSettings = {}
          this.projectPlatforms.forEach(platform => {
            const isEnabled = values['enable_' + platform] !== undefined ? values['enable_' + platform] : true
            const isOverridden = values['override_' + platform]

            const platContentMap = {
              'zh-Hans': values['content_zh_Hans_' + platform] || '',
              'zh-Hant': values['content_zh_Hant_' + platform] || '',
              'en': values['content_en_' + platform] || ''
            }
            const hasPlatContent = platContentMap['zh-Hans'] || platContentMap['zh-Hant'] || platContentMap['en']

            platformSettings[platform] = {
              enable: isEnabled,
              download_url: isOverridden ? (values['download_url_' + platform] || '') : '',
              content: isOverridden && hasPlatContent ? JSON.stringify(platContentMap) : '',
              config: isOverridden ? (values['config_' + platform] || '') : ''
            }
            // 清理动态表单字段，不提交到后端对应的同名顶层字段
            delete values['enable_' + platform]
            delete values['override_' + platform]
            delete values['download_url_' + platform]
            delete values['content_zh_Hans_' + platform]
            delete values['content_zh_Hant_' + platform]
            delete values['content_en_' + platform]
            delete values['config_' + platform]
          })
          values.platform_settings = platformSettings

          if (values.id) {
            // 更新
            updateVersion(values).then(res => {
              this.visible = false
              // 重置表单数据
              form.resetFields()
              // 刷新表格
              this.$refs.table.refresh()

              this.$message.success('更新成功')
            })
            .catch(err => {
              console.error('更新版本失败', err)
            })
            .finally(() => {
              this.confirmLoading = false
            })
          } else {
            // 新增
            createVersion(values).then(res => {
              this.visible = false
              // 重置表单数据
              form.resetFields()
              // 刷新表格
              this.$refs.table.refresh()

              this.$message.success('创建成功')
            })
            .catch(err => {
              console.error('新建版本失败', err)
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
    loadProjectDetails () {
      getProject(this.projectId).then(res => {
        this.projectPlatforms = res.data.platforms ? res.data.platforms.split(',') : []
      }).catch(err => {
        console.error('获取项目支持平台失败', err)
        this.projectPlatforms = []
      })
    },
    handleCancel () {
      this.visible = false

      const form = this.$refs.createModal.form
      form.resetFields() // 清理表单数据（可不做）
    },
    handleDelete (record) {
      deleteVersion(record.id).then(res => {
        this.$refs.table.refresh()
        this.$message.success('删除成功')
      }).catch(err => {
        console.error('删除版本失败', err)
      })
    },
    onSelectChange (selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys
      this.selectedRows = selectedRows
    },
    onCopySuccess () {
      this.$message.success('已复制到剪贴板')
    },
    formatChangelog (content) {
      if (!content) return '-'
      const trimmed = content.trim()
      if (trimmed.startsWith('{') && trimmed.endsWith('}')) {
        try {
          const map = JSON.parse(content)
          return map['zh-Hans'] || Object.values(map)[0] || '-'
        } catch (e) {
          return content
        }
      }
      return content
    }
  }
}
</script>
