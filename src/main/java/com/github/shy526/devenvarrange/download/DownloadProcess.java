package com.github.shy526.devenvarrange.download;

import com.github.shy526.devenvarrange.oo.ToolVersion;

import java.util.List;

public interface DownloadProcess {
    List<ToolVersion> getVersion(String url);
}
