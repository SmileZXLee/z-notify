class Cache{
    static get(){
		return uni.getStorageSync(this.getCacheKey())
    }
    static set(v){
		uni.setStorageSync(this.getCacheKey(), v);
    }

    static getCacheKey(){
        return 'cache';
    }

    static getUserCacheKey(key){
        return key + '-' + (localStorage.getItem('account') || 'public');
    }
}

// --------------------------------------------------------

// 用户名
class Username extends Cache{
    static getCacheKey(){
        return this.getUserCacheKey('cachedUsername');
    }
}

// 项目id
class ProjectId extends Cache{
    static getCacheKey(){
        return this.getUserCacheKey('cachedProjectId');
    }
}

// 联系方式
class Contact extends Cache {
    static getCacheKey(){
        return this.getUserCacheKey('cachedContact');
    }
}

// 附加信息
class ExtraInfo extends Cache {
    static getCacheKey(){
        return this.getUserCacheKey('cachedExtraInfo');
    }
}

// 联系方式类型
class ContactType extends Cache {
    static getCacheKey(){
        return this.getUserCacheKey('cachedContactType');
    }
}

export default {
    ProjectId,
    Username,
	ExtraInfo,
    Contact,
	ContactType
}

