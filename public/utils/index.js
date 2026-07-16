import md5 from './encryp/md5.js'
import consts from '../const'
function showToast(title) {
	uni.showToast({
		title,
		icon: 'none'
	})
}

function showToastAndBack(title) {
	showToast(title);
	setTimeout(() => {
		uni.navigateBack({
			delta: 1
		})
	},200)
}

function isLogined() {
	return !!uni.getStorageSync(consts.TOKEN_STORAGE_KEY);
}

function md5Hex(str) {
	return md5.hex_md5(str);
}

function timestamp2DateStr(timestamp) {
	const date = new Date(timestamp)
	const Y = date.getFullYear();
	const M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1);
	const D = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate()) ;

	const h = (date.getHours() < 10 ? '0' + (date.getHours()) : date.getHours());
	const m = (date.getMinutes() < 10 ? '0' + (date.getMinutes()) : date.getMinutes());
	const s = (date.getSeconds() < 10 ? '0' + (date.getSeconds()) : date.getSeconds());
	return `${Y}-${M}-${D} ${h}:${m}:${s}`;
}

function isQQNum(str){
	let reg = /^[1-9][0-9]{4,9}$/gim;
	return reg.test(str);
}

function isWXNum(str){
	let reg = /^[a-zA-Z]([-_a-zA-Z0-9]{5,19})+$/;
	return reg.test(str);
}

function isPhoneNum(str){
	let reg = /^1[34578]\d{9}$/;
	return reg.test(str);
}

module.exports = {
	showToast,
	showToastAndBack,
	isLogined,
	md5Hex,
	timestamp2DateStr,
	isQQNum,
	isWXNum,
	isPhoneNum
}