package com.github.davidmoten.msgraph;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import odata.msgraph.client.complex.EmailAddress;
import odata.msgraph.client.complex.ItemBody;
import odata.msgraph.client.complex.Recipient;
import odata.msgraph.client.container.GraphService;
import odata.msgraph.client.entity.Message;
import odata.msgraph.client.entity.request.MailFolderRequest;
import odata.msgraph.client.enums.BodyType;

public class MsGraphMain {

    public static void main(String[] args) {
        
        // this test creates 
        System.setProperty("https.proxyHost", "proxy.amsa.gov.au");
        System.setProperty("https.proxyPort", "8080");

        GraphService client = MsGraph //
                .tenantName(System.getProperty("tenantName")) //
                .clientId(System.getProperty("clientId")) //
                .clientSecret(System.getProperty("clientSecret")) //
                .refreshBeforeExpiry(5, TimeUnit.MINUTES) //
                .build();

        String mailbox = "dnex001@amsa.gov.au";
        MailFolderRequest drafts = client //
                .users(mailbox) //
                .mailFolders("Drafts");
        long count = drafts.messages() //
                .metadataNone() //
                .get() //
                .stream() //
                .count(); //
        System.out.println("count in Drafts folder=" + count);

        String id = UUID.randomUUID().toString().substring(0, 6);
        Message m = Message.createMessage().withSubject(Optional.of("hi there " + id))
                .withBody(Optional.of(ItemBody.builder() //
                        .content("hello there how are you") //
                        .contentType(BodyType.TEXT).build())) //
                .withFrom(Optional.of(Recipient.builder()
                        .emailAddress(EmailAddress.builder().address("dnex001@amsa.gov.au").build()).build()));

        // Create the draft message
        Message saved = drafts.messages().post(m);

        // change subject
        saved.getUnmappedFields().entrySet().forEach(System.out::println);

        client.users(mailbox).messages(saved.getId().get()).patch(saved.withSubject(Optional.of("new subject " + id)));

        String amendedSubject = drafts.messages(saved.getId().get()).get().getSubject().get();
        if (!("new subject " + id).equals(amendedSubject)) {
            throw new RuntimeException("subject not amended");
        }
        
        long count2 = drafts.messages() //
                .metadataNone() //
                .get() //
                .stream() //
                .count(); //
        if (count2 != count + 1) {
            throw new RuntimeException("unexpected count");
        }

        // Delete the draft message
        drafts.messages(saved.getId().get()) //
                .delete();
    }

}
