<template>
	<view class="feedback-item-content">
		<view class="feedback-item-main">
			<view class="feedback-item-group">
				<view class="feedback-item-title">
					联系方式
				</view>
				<view class="feedback-item-text">
					{{item.contact}}
				</view>
			</view>
			<view class="feedback-item-group">
				<view class="feedback-item-title">
					反馈内容
				</view>
				<view class="feedback-item-text">
					{{item.content}}
				</view>
				<view class="feedback-item-imgs" v-if="item.img_urls && item.img_urls.length">
					<view class="feedback-item-img" v-for="(subItem,subIndex) in item.img_urls" @click="imgClick(item,subIndex)">
						<u-image :src="subItem" height="100" width="100" radius="5">
						</u-image>
					</view>
				</view>
			</view>
			<view class="feedback-item-group">
				<view class="feedback-item-title">
					创建时间
				</view>
				<view class="feedback-item-text">
					{{item.createtime}}
				</view>
			</view>
			<!-- <view class="feedback-item-time">{{item.createtime}}</view> -->
            <view style="margin-top: 15rpx;" v-if="item.reply && item.reply.length">
                <u-alert type="primary" title="开发者回复" :description="item.reply" style="margin-right: 30rpx;"></u-alert>
            </view>
		</view>
    </view>
</template>

<script>
	export default {
		name: "feedback-item",
		data() {
			return {
				
			};
		},
		props: ['item'],
		methods: {
			imgClick(item, index) {
				uni.previewImage({
					current: index,
					urls: item.img_urls,
				});
			}
		}
	}
</script>

<style lang="scss" scoped>
	.feedback-item-content {
		position: relative;
		display: flex;
		padding: 0rpx 30rpx;
		margin-top: 30rpx;
		.feedback-item-main {
			padding: 30rpx;
			padding-right: 0rpx;
			flex: 1;
			box-shadow: 2px 0px 20rpx rgba(0, 0, 0, 0.1);
			border-radius: 20rpx;
			.feedback-item-group {
				margin-bottom: 10rpx;
				.feedback-item-title {
					font-size: 28rpx;
					font-weight: bold;
				}
				.feedback-item-text {
					padding: 5rpx 0rpx;
					font-size: 26rpx;
					word-break: break-all;
					.feedback-item-text-parse{
						width: 100%;
					}
				}
				.feedback-item-imgs {
					display: flex;
					flex-wrap: wrap;
					margin-top: 20rpx;
					.feedback-item-img {
						margin-right: 20rpx;
						margin-bottom: 20rpx;
						border-radius: 10rpx;
					}
				}
			}

			.feedback-item-time {
				font-size: 24rpx;
				color: $uni-text-color-grey;
				margin-top: 20rpx;
			}
			.feedback-item-operation{
				display: flex;
				justify-content: space-between;
				margin-top: 20rpx;
				font-size: 26rpx;
				.feedback-item-status{
					
				}
				.feedback-item-delete{
					color: red;
				}
				.feedback-item-options{
					display: flex;
					align-items: center;
				}
			}
		}
		.feedback-item-line {
			position: absolute;
			width: 100%;
			height: 1px;
			left: 0;
			bottom: 0;
			background-color: $u-primary-light;
		}
	}
</style>
