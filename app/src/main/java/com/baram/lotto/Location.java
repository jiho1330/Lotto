package com.baram.lotto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Location {
    @SerializedName("documents")
    public List<Document> documentsList;

    @SerializedName("meta")
    public Meta meta;

    public static class Document {
        @SerializedName("address")
        private Address address;
        @SerializedName("address_name") // 지번
        private String address_name;
        @SerializedName("road_address_name")    // 도로명
        private String road_address_name;
        @SerializedName("x")        //longitude
        private String x;
        @SerializedName("y")        //latitude
        private String y;
        @SerializedName("place_name")   // 상호명
        private String place_name;
        @SerializedName("place_url")    // 장소 URL
        private String place_url;
        @SerializedName("distance") // 거리
        private int distance;
        @SerializedName("phone")    // 전화번호
        private String phone;

        public static class Address {
            @SerializedName("h_code")
            private String h_code;          //행정코드

            public String getH_code() {
                return h_code;
            }

            public void setH_code(String h_code) {
                this.h_code = h_code;
            }
        }

        public Address getAddress() {
            return address;
        }
        public String getAddress_name() {
            return address_name;
        }
        public String getRoad_address_name() { return road_address_name; }
        public String getX() {
            return x;
        }
        public String getY() {
            return y;
        }
        public String getPlace_name() {
            return place_name;
        }
        public String getPlace_url() {
            return place_url;
        }
        public int getDistance() { return distance; }
        public String getPhone() { return phone; }
    }


    public static class Meta {
        @SerializedName("is_end")
        private boolean is_end;
        @SerializedName("pageable_count")
        private int pageable_count;
        @SerializedName("total_count")
        private int total_count;

        public boolean isIs_end() {
            return is_end;
        }

        public void setIs_end(boolean is_end) {
            this.is_end = is_end;
        }

        public int getPageable_count() {
            return pageable_count;
        }

        public void setPageable_count(int pageable_count) {
            this.pageable_count = pageable_count;
        }

        public int getTotal_count() {
            return total_count;
        }

        public void setTotal_count(int total_count) {
            this.total_count = total_count;
        }
    }
}
