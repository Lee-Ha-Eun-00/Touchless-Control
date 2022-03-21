package com.teamSLL.mlkit.adapter;

public class VideoInfo {
    public String videoThumbnail;
    public String channelThumbnail;
    public String videoTitle;
    public String channelID;
    public String videoViews;
    public String uploadedTime;

    public VideoInfo(String videoThumbnail, String channelThumbnail, String videoTitle, String channelID, String videoViews, String uploadedTime){
        this.videoThumbnail = videoThumbnail;
        this.channelThumbnail = channelThumbnail;
        this.videoTitle = videoTitle;
        this.channelID = channelID;
        this.videoViews = videoViews;
        this.uploadedTime = uploadedTime;
    }
}
