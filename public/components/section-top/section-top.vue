<template>
	<view class="section-top">
		<view class="section-left">
			<view class="section-line"></view>
			<view class="section-title">{{title}}</view>
		</view>
		<view class="section-right" @click="addClick">
			<u-icon name="plus-circle" color="#3c9cff" size="20" />
		</view>
	</view>
</template>

<script>
	export default {
		name:"section-top",
		props: {
			title: ''
		},
		data() {
			return {
				
			};
		},
		methods: {
			addClick(){
				this.$emit('addClick');
			}
		}
	}
</script>

<style lang="scss">
	.section-top{
		display: flex;
		justify-content: space-between;
		align-items: center;
		.section-left{
			display: flex;
			align-items: center;
			.section-line{
				width: 10rpx;
				height: 34rpx;
				border-radius: 4rpx;
				background-color: $u-primary;
			}
			.section-title{
				font-size: 32rpx;
				font-weight: bold;
				margin-left: 15rpx;
			}
		}
		.section-right{
			font-size: 24rpx;
			color: #999999;
		}
	}
</style>