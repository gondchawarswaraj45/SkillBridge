package com.freelancing.service;

import com.freelancing.db.DatabaseManager;
import com.freelancing.model.Milestone;
import com.freelancing.model.Project;
import java.util.UUID;

public class PaymentService {
    private final DatabaseManager db = DatabaseManager.getInstance();

    public static class PaymentReceipt {
        public String transactionId;
        public String razorpayPaymentId;
        public String milestoneTitle;
        public double amount;
        public String currency = "INR / USD";
        public String timestamp;
        public String status = "SUCCESS";

        @Override
        public String toString() {
            return "===========================================\n" +
                   "         RAZORPAY PAYMENT RECEIPT          \n" +
                   "===========================================\n" +
                   " Transaction ID : " + transactionId + "\n" +
                   " Razorpay ID    : " + razorpayPaymentId + "\n" +
                   " Milestone      : " + milestoneTitle + "\n" +
                   " Amount Released: $" + String.format("%.2f", amount) + "\n" +
                   " Status         : " + status + "\n" +
                   " Date           : " + timestamp + "\n" +
                   "===========================================\n" +
                   " Thank you for using Freelancing.SB Platform!";
        }
    }

    public PaymentReceipt processRazorpayPayment(Milestone milestone, Project project) {
        String txId = "TXN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String rzpId = "pay_rzp_" + UUID.randomUUID().toString().substring(0, 10);

        milestone.setStatus(Milestone.Status.PAID);
        db.getMilestones().put(milestone.getId(), milestone);

        PaymentReceipt receipt = new PaymentReceipt();
        receipt.transactionId = txId;
        receipt.razorpayPaymentId = rzpId;
        receipt.milestoneTitle = milestone.getTitle();
        receipt.amount = milestone.getAmount();
        receipt.timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

        db.logActivity("Payment released via Razorpay: $" + milestone.getAmount() + " for milestone '" + milestone.getTitle() + "' (Txn: " + txId + ")");
        db.saveData();

        return receipt;
    }
}
