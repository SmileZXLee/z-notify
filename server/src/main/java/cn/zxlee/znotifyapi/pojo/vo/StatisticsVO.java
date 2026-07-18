package cn.zxlee.znotifyapi.pojo.vo;

import cn.zxlee.znotifyapi.pojo.BasePOJO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @program: z-notify-api
 * @description: StatisticsBO
 * @author: zxlee
 * @create: 2022-12-17 17:05
 **/

@Data
@ApiModel("统计VO")
public class StatisticsVO extends BasePOJO {
    @ApiModelProperty("项目id")
    private String projectId;
    @ApiModelProperty("访问者的ip地址")
    private String ip;

    public String getIp() {
        if (this.ip == null || this.ip.isEmpty()) {
            return this.ip;
        }
        if (this.ip.contains(".")) {
            int lastDotIndex = this.ip.lastIndexOf(".");
            if (lastDotIndex > 0) {
                return this.ip.substring(0, lastDotIndex) + ".*";
            }
        } else if (this.ip.contains(":")) {
            int lastColonIndex = this.ip.lastIndexOf(":");
            if (lastColonIndex > 0) {
                return this.ip.substring(0, lastColonIndex) + ":*";
            }
        }
        return this.ip;
    }

    @ApiModelProperty("访问者的ip归属地")
    private String ipRegion;
    @ApiModelProperty("用于额外区分不同个体的标签")
    private String tag;
    @ApiModelProperty("访问者来源")
    private String from;
}
