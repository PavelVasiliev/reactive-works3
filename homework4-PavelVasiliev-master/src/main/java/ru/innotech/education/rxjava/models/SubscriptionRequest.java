package ru.innotech.education.rxjava.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SubscriptionRequest {
    private String link;

    public static SubscriptionRequest subscriptionRequest(String link) {
        return new SubscriptionRequest(link);
    }
}
