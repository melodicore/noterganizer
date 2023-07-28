package me.datafox.noterganizer.client.serialization;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.net.HttpCookie;

/**
 * A simple {@link com.google.gson.Gson} type adapter for a {@link HttpCookie}
 *
 * @author datafox
 */
public class HttpCookieAdapter extends TypeAdapter<HttpCookie> {
    @Override
    public void write(JsonWriter out, HttpCookie cookie) throws IOException {
        out.beginObject()
                .name("name")
                .value(cookie.getName())
                .name("value")
                .value(cookie.getValue())
                .name("comment")
                .value(cookie.getComment())
                .name("commentURL")
                .value(cookie.getCommentURL())
                .name("toDiscard")
                .value(cookie.getDiscard())
                .name("domain")
                .value(cookie.getDomain())
                .name("maxAge")
                .value(cookie.getMaxAge())
                .name("path")
                .value(cookie.getPath())
                .name("portlist")
                .value(cookie.getPortlist())
                .name("secure")
                .value(cookie.getSecure())
                .name("httpOnly")
                .value(cookie.isHttpOnly())
                .name("version")
                .value(cookie.getVersion())
                .endObject();
    }

    @Override
    public HttpCookie read(JsonReader in) throws IOException {
        HttpCookieBuilder builder = new HttpCookieBuilder();
        in.beginObject();
        String field = null;
        while(in.hasNext()) {
            JsonToken token = in.peek();

            if(token.equals(JsonToken.NAME)) {
                field = in.nextName();
            }

            if(field != null) switch(field) {
                case "name" -> builder.name(in.nextString());
                case "value" -> builder.value(in.nextString());
                case "comment" -> builder.comment(in.nextString());
                case "commentURL" -> builder.commentURL(in.nextString());
                case "toDiscard" -> builder.toDiscard(in.nextBoolean());
                case "domain" -> builder.domain(in.nextString());
                case "maxAge" -> builder.maxAge(in.nextLong());
                case "path" -> builder.path(in.nextString());
                case "portlist" -> builder.portlist(in.nextString());
                case "secure" -> builder.secure(in.nextBoolean());
                case "httpOnly" -> builder.httpOnly(in.nextBoolean());
                case "version" -> builder.version(in.nextInt());
                default -> in.nextNull();
            }
        }
        in.endObject();
        return builder.build();
    }

    public static class HttpCookieBuilder {
        private String name;
        private String value;
        private String comment;
        private String commentURL;
        private boolean toDiscard;
        private String domain;
        private long maxAge;
        private String path;
        private String portlist;
        private boolean secure;
        private boolean httpOnly;
        private int version;

        HttpCookieBuilder() {
        }

        public HttpCookieBuilder name(String name) {
            this.name = name;
            return this;
        }

        public HttpCookieBuilder value(String value) {
            this.value = value;
            return this;
        }

        public HttpCookieBuilder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public HttpCookieBuilder commentURL(String commentURL) {
            this.commentURL = commentURL;
            return this;
        }

        public HttpCookieBuilder toDiscard(boolean toDiscard) {
            this.toDiscard = toDiscard;
            return this;
        }

        public HttpCookieBuilder domain(String domain) {
            this.domain = domain;
            return this;
        }

        public HttpCookieBuilder maxAge(long maxAge) {
            this.maxAge = maxAge;
            return this;
        }

        public HttpCookieBuilder path(String path) {
            this.path = path;
            return this;
        }

        public HttpCookieBuilder portlist(String portlist) {
            this.portlist = portlist;
            return this;
        }

        public HttpCookieBuilder secure(boolean secure) {
            this.secure = secure;
            return this;
        }

        public HttpCookieBuilder httpOnly(boolean httpOnly) {
            this.httpOnly = httpOnly;
            return this;
        }

        public HttpCookieBuilder version(int version) {
            this.version = version;
            return this;
        }

        public HttpCookie build() {
            HttpCookie cookie = new HttpCookie(name, value);
            cookie.setComment(comment);
            cookie.setCommentURL(commentURL);
            cookie.setDiscard(toDiscard);
            cookie.setDomain(domain);
            cookie.setMaxAge(maxAge);
            cookie.setPath(path);
            cookie.setPortlist(portlist);
            cookie.setSecure(secure);
            cookie.setHttpOnly(httpOnly);
            cookie.setVersion(version);
            return cookie;
        }

        public String toString() {
            return "HttpCookieAdapter.HttpCookieBuilder.HttpCookieBuilderBuilder(name=" + this.name + ", value=" + this.value + ", comment=" + this.comment + ", commentURL=" + this.commentURL + ", toDiscard=" + this.toDiscard + ", domain=" + this.domain + ", maxAge=" + this.maxAge + ", path=" + this.path + ", portlist=" + this.portlist + ", secure=" + this.secure + ", httpOnly=" + this.httpOnly + ", version=" + this.version + ")";
        }
    }
}
