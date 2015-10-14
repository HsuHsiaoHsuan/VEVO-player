package idv.hsu.vevoplayer.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Snippet {
    public Snippet() {
    }

    private String publishedAt;
    private String title;
    private String description;
    private ResourceId resourceId;
    private String channelId;
    private Thumbnails thumbnails;
    private Localized localized;

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ResourceId getResourceId() {
        return resourceId;
    }

    public void setResourceId(ResourceId resourceId) {
        this.resourceId = resourceId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public Thumbnails getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(Thumbnails thumbnails) {
        this.thumbnails = thumbnails;
    }

    public Localized getLocalized() {
        return localized;
    }

    public void setLocalized(Localized localized) {
        this.localized = localized;
    }

    public class ResourceId {
        public ResourceId() {}
        private String kind;
        private String channelId;

        private String videoId;

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }
    }

    public class Thumbnails {
        public Thumbnails() {}

        @JsonProperty(value = "default")
        private Default def;        // subscriptions, playlists
        private Medium medium;      // playlists
        private High high;          // subscriptions, playlists
        private Standard standard;  // playlists
        private Maxres maxres;      // playlists

        public Medium getMedium() {
            return medium;
        }

        public void setMedium(Medium medium) {
            this.medium = medium;
        }

        public Standard getStandard() {
            return standard;
        }

        public void setStandard(Standard standard) {
            this.standard = standard;
        }

        public Maxres getMaxres() {
            return maxres;
        }

        public void setMaxres(Maxres maxres) {
            this.maxres = maxres;
        }

        public Default getDef() {
            return def;
        }

        public void setDef(Default def) {
            this.def = def;
        }

        public High getHigh() {
            return high;
        }

        public void setHigh(High high) {
            this.high = high;
        }

        public class Default {
            public Default() {
            }

            private String url;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }

        public class Medium {
            public Medium() {
            }

            private String url;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }

        public class High {
            public High() {
            }

            private String url;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }

        public class Standard {
            public Standard() {
            }

            private String url;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }

        public class Maxres {
            public Maxres() {
            }

            private String url;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }

    public class Localized {
        public Localized() {
        }

        private String title;
        private String description;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}



