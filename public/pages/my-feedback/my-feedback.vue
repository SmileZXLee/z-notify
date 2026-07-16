<template>
	<view class="content">
		<z-paging ref="paging" v-model="list" @query="queryList" @scroll="scroll">
			<feedback-item v-for="(item,index) in list" :item="item"></feedback-item>
		</z-paging>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				projectId: '',
				username: '',
				list: [],
				userData: null,
				showMore: false,
				showLogout: false,
				moreList: [{
					text: '退出登录',
					color: 'red'
				}]
			}
		},
		onLoad(options) {
			this.projectId = options.project_id;
			this.username = options.username;
			uni.$on('updateList', () => {
				this.$refs.paging.reload();
			})
		},
		destroyed() {
			uni.$off('updateList');
		},
		onShow() {
			if (this.$utils.isLogined()) {
				this.$api.userinfo({}).then(res => {
					this.userData = res.data;
					if(this.userData.authorities && this.userData.authorities.length){
						if(this.userData.authorities.indexOf('information:examine') !== -1){
							this.moreList = [{
								text: '退出登录',
								color: 'red'
							},{
								text: '审核中心'
							}]
						}
					}
					uni.setNavigationBarTitle({
						title: res.data.username + '-信息平台'
					})
				})
			}
		},
		methods: {
			scroll(e) {
				const scrollTop = e.detail.scrollTop;
				let rate = 0;
				const maxHeight = uni.upx2px(450);
				rate = scrollTop / maxHeight;
				if (rate > 1) {
					rate = 1;
				}
				if (rate < 0) {
					rate = 0;
				}
			},
			queryList(current, pageSize) {
				this.$api.getFeedbackList(this.projectId, this.username).then(res => {
					this.$refs.paging.complete(res.data);
				}).catch(() => {
					this.$refs.paging.complete(false);
				})
			},
			del(index) {
				this.list.splice(index, 1);
			},
			publishClick() {
				if (this.$utils.checkLoginedAndGo()) {
					uni.navigateTo({
						url: '../publish/publish?formMe=true'
					})
				}
			},
			moreClick() {
				this.showMore = true;
			},
			moreNextClick(index) {
				if (index === 0) {
					//退出登录
					this.showLogout = true;
				}else if(index === 1){
					uni.navigateTo({
						url: '../audit-center/audit-center'
					})
				}
			},
			logoutClick() {
				this.$api.logout({}).then(res => {
					uni.removeStorageSync('token');
					uni.navigateBack({
						delta: 1
					})
				})
			}
		}
	}
</script>

<style lang="scss" scoped>
	.content {
		position: relative;
		padding-bottom: var(--window-bottom);

		.me-header {
			height: 450rpx;
			background-color: red;
		}

		.add-new-view {
			position: fixed;
			bottom: 100rpx;
			right: 30rpx;
			background-color: white;
			border-radius: 50%;

			.add-new {
				font-size: 100rpx;
				color: $uni-color-primary;
			}
		}
		
		.more-view {
			position: fixed;
			bottom: 300rpx;
			right: 30rpx;
			background-color: white;
			border-radius: 50%;
			.more {
				font-size: 93rpx;
				color: $uni-color-primary;
			}
		}

		.slot-wrap {
			display: flex;
			flex: 1;
			justify-content: flex-end;
		}
	}
</style>
