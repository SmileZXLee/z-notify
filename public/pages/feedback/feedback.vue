<template>
	<view class="content">
		<u-navbar title="提交反馈" :titleStyle="{'font-weight': 700, color: 'black'}" placeholder leftIcon="" :rightIcon="form.username.length ? 'list-dot' : ''" @rightClick="toMyFeedback">
		</u-navbar>
		<u-alert v-if="!!errorTip" :title="errorTip" type="error" :show-icon="true"></u-alert>
		<view class="feedback-concat">
			<view class="feedback-concat-item">
				<view class="feedback-concat-item-title">用户名(必填)</view>
				<view class="feedback-concat-item-main">
					<u-input border="none" maxlength="20" :disabled="!!username" placeholder="请输入用户名" clearable v-model="form.username"></u-input>
				</view>
			</view>
			<view class="feedback-concat-item">
				<view class="feedback-concat-item-title">联系类型</view>
				<view class="feedback-concat-item-main">
					<u-radio-group v-model="form.contactType">
						<u-radio active-color="#2ea1ff" style="margin-right: 20rpx;" @change="contactRadioChange"
							v-for="(item, index) in contactMethods" :key="index" :name="item.value">
							{{item.name}}
						</u-radio>
					</u-radio-group>
				</view>
			</view>
			<view class="feedback-concat-item">
				<view class="feedback-concat-item-title">联系方式</view>
                <view class="feedback-concat-item-subtitle" v-if="form.contactType!==0">若不想提供联系方式，联系类型请选择"无"</view>
				<view class="feedback-concat-item-main">
					<u-input border="none" :placeholder="form.contactType===0?'无联系方式':'请输入联系方式'" clearable v-model="form.contact"
						:disabled="form.contactType===0"></u-input>
				</view>
			</view>
			<view class="feedback-concat-item">
				<view class="feedback-concat-item-title">反馈内容(必填)</view>
				<view class="feedback-concat-item-main">
					<u--textarea border="none" count maxlength="500" v-model="form.content" placeholder="请输入反馈内容" ></u--textarea>
				</view>
			</view>
			<u-upload :fileList="form.img_urls" @afterRead="afterRead" @delete="deletePic" ref="uUpload" multiple :size-type="['compressed']" name="files"
				:action="action" max-count="6">
			</u-upload>
		</view>
		<u-button class="submit-btn" :disabled="!!errorTip" :loading="loading"  type="primary" shape="circle" text="提交" @click="feedbackClick"></u-button>
		<view class="footer">
			{{ showFooter ? '—— 由Z-Notify提供技术支持 ——' : ''}}
		</view>
		<w-compress ref='wCompress' />
	</view>
</template>

<script>
	import commonConfig from '@/config/index.js'
	export default {
		data() {
			return {
				loading: false,
				showFooter: true,
				errorTip: '',
				username: '',
				action: commonConfig.BASE_URL + '/v1/public/upload/uploadFiles',
				contactMethods: [{
						name: 'QQ',
						value: 2
					},
					{
						name: '微信',
						value: 3
					},
					{
						name: '手机',
						value: 1
					},
					{
						name: '无',
						value: 0
					}
				],
				form: {
					project_id: '',
					username: '',
					contactType: 2,
					contact: '',
					content: '',
					img_urls: [],
					extra_info: ''
				},
			}
		},
		onLoad(options) {
			this.initData(options);
			if (!this.form.project_id) {
				this.errorTip = '缺少project_id，请检查url配置！';
			}
		},
		methods: {
			initData(options) {
				if (this.$cache.Username.get()) {
					this.form.username = this.$cache.Username.get();
					this.username = this.$cache.Username.get();
				}
				if (this.$cache.ProjectId.get()) {
					this.form.project_id = this.$cache.ProjectId.get();
				}
				if (this.$cache.Contact.get()) {
					this.form.contact = this.$cache.Contact.get();
				}
				if (this.$cache.ContactType.get()) {
					this.form.contactType = this.$cache.ContactType.get();
				}
				if (this.$cache.ExtraInfo.get()) {
					this.form.extra_info = this.$cache.ExtraInfo.get();
				}
				if (options.project_id) {
					this.form.project_id = options.project_id;
					this.$cache.ProjectId.set(options.project_id);
				}
				if (options.username) {
					this.username = options.username;
					this.form.username = options.username;
					this.$cache.Username.set(options.username);
				}
				if (options.show_footer) {
					this.showFooter = options.show_footer;
				}
			},
			contactRadioChange(e) {
				if (e === 0) {
					this.form.contact = '';
				}
			},
			toMyFeedback() {
				if (this.errorTip || !this.form.username) return;
				uni.navigateTo({
					url: `../my-feedback/my-feedback?project_id=${this.form.project_id}&username=${this.form.username}`
				})
			},
			feedbackClick() {
				if (!this.form.username.length) {
					this.$utils.showToast('请输入用户名');
					return;
				}
				if (this.form.contactType !== 0) {
					if (!this.form.contact.length) {
						this.$utils.showToast('请输入联系方式');
						return;
					}
				}
				if (!this.form.content.length) {
					this.$utils.showToast('请输入反馈内容');
					return;
				}
				if (this.form.contactType === 1) {
					//手机号
					if (!this.$utils.isPhoneNum(this.form.contact)) {
						this.$utils.showToast('手机号格式不正确');
						return;
					}
				} else if (this.form.contactType === 2) {
					//QQ号
					if (!this.$utils.isQQNum(this.form.contact)) {
						this.$utils.showToast('QQ号格式不正确');
						return;
					}
				} else if (this.form.contactType === 3) {
					//微信号
					if (!this.$utils.isWXNum(this.form.contact)) {
						this.$utils.showToast('微信号格式不正确');
						return;
					}
				}
				
				const form = { ...this.form };
				if (form.contactType !== 0) {
					form.contact = `${this.contactMethods.find(i => i.value === form.contactType).name}: ${form.contact}`;
				}
				if (!!form.img_urls) {
					form.img_urls = form.img_urls.map(file => file.url);
				}
				this.loading = true;
				if (!form.extra_info) {
					const { deviceModel, deviceType, model, osName, osVersion, platform } = uni.getSystemInfoSync();
					form.extra_info = JSON.stringify({ deviceModel, deviceType, model, osName, osVersion, platform });
				}
				this.$api.feedback(form).then(res => {
					this.$cache.Contact.set(this.form.contact);
					this.$cache.ContactType.set(this.form.contactType);
					this.$utils.showToast('反馈提交成功！');
					setTimeout(() => {
						this.loading = false;
						this.form.content = '';
						this.form.img_urls = [];
						this.toMyFeedback();
					}, 1000)
				}).catch(res => {
					this.loading = false;
				})
			},
			// 删除图片
			deletePic(event) {
				this.form.img_urls.splice(event.index, 1)
			},
			// 新增图片
			async afterRead(event) {
				let lists = [].concat(event.file)
				let fileListLen = this.form.img_urls.length
				lists.map((item) => {
					this.form.img_urls.push({
						...item,
						status: 'uploading',
						message: '上传中'
					})
				})
				for (let i = 0; i < lists.length; i++) {
					const result = await this.uploadFilePromise(lists[i].url)
					let item = this.form.img_urls[fileListLen]
					this.form.img_urls.splice(fileListLen, 1, Object.assign(item, {
						status: !!result ? 'success' : 'failed',
						message: '',
						url: result
					}))
					fileListLen++;
				}
			},
			uploadFilePromise(url) {
				return new Promise((resolve, reject) => {
					this.$refs.wCompress.start(url, {
						pixels: 1000000
					})
					.then(res => {
						uni.uploadFile({
							url: this.action,
							filePath: res,
							name: 'files',
							success: (res) => {
								resolve(JSON.parse(res.data).data[0]);
							},
							fail: (err) => {
								resolve(null);
							}
						});
					})
					.catch(e => {
						resolve(null);
					})
				})
			},
		}
	}
</script>

<style lang="scss" scoped>
	.feedback-concat {
		padding: 30rpx;

		.feedback-concat-item {
			background-color: $uni-bg-color-grey;
			padding: 20rpx;
			border-radius: 12rpx;
			margin-bottom: 20rpx;

			.feedback-concat-item-title {
				font-size: 34rpx;
				font-weight: bold;
			}
            
            .feedback-concat-item-subtitle{
                font-size: 24rpx;
                color: #4d4d4d;
                margin-top: 5rpx;
            }

			.feedback-concat-item-main {
				margin-top: 20rpx;
			}
		}
	}

	.submit-btn {
		width: calc(100% - 100rpx);
		margin-top: 20rpx;
	}
	
	.footer{
		font-size: 24rpx;
		color: #cccccc;
		padding: 80rpx 0px 20rpx 0px;
		text-align: center;
	}
</style>
