package DataModel;

import java.time.LocalDate;
import java.util.ArrayList;

public class User implements MessageObserver {

    private String id;
    private String username;
    private String password;
    private String role;
    private Member member;
    private int points;
    private LocalDate lastSignInDate;
    private ArrayList<OrderRecord> orderRecordList;
    private ArrayList<Coupon> couponList;

    private String latestAnnouncement;

    public User(String role, String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.member = new NormalMember();
        orderRecordList = new ArrayList<OrderRecord>();
        lastSignInDate = null;
        points = 0;
        latestAnnouncement = null;
        couponList = new ArrayList<Coupon>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public String getMemberType() {
        return member.getMemberType();
    }

    public double getDiscount() {
        return member.getDiscount();
    }

    public LocalDate getLastSignInDate() {
        return lastSignInDate;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public void setLastSignInDate(LocalDate lastSignInDate) {
        this.lastSignInDate = lastSignInDate;
    }

    public void addCoupon(Coupon coupon) {
        couponList.add(coupon);
    }

    public void removeCoupon(Coupon coupon) {
        couponList.remove(coupon);
    }

    public ArrayList<Coupon> getCouponList() {
        return couponList;
    }

    public Member getMember() {
        return member;
    }

    public String toString() {
        return "User [id=" + id + ", username=" + username + ", password=" + password + ", role=" + role + ", member="
                + member + ", points=" + points + ", lastSignInDate=" + lastSignInDate + ", orderRecordList="
                + orderRecordList + "]";
    }

    public String getLatestAnnouncement() {
        return latestAnnouncement;
    }

    // update the latest announcement from Admin
    @Override
    public void update(String message) {
        latestAnnouncement = message;
    }

    public boolean isReceivedAnnouncement() {
        return latestAnnouncement != null;
    }

}