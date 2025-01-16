package com.github.thiagogarbazza.pocs.poc_microsoft_graph;

import com.microsoft.graph.drives.item.items.item.invite.InvitePostRequestBody;
import com.microsoft.graph.models.DriveRecipient;
import com.microsoft.graph.models.Entity;
import com.microsoft.graph.models.PermissionCollectionResponse;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
class MicrosoftGraphFilePermissionService {

    private static final int EXPIRATION_DAYS = 10;

    private final GraphServiceClient graphServiceClient;

    public void add(final String driveId, final String driveItemId, final String emailUsuario, final String mensagem) {
        final DriveRecipient driveRecipient = new DriveRecipient();
        driveRecipient.setEmail(emailUsuario);

        final InvitePostRequestBody invitePostRequestBody = new InvitePostRequestBody();
        invitePostRequestBody.setRecipients(List.of(driveRecipient));
        invitePostRequestBody.setMessage(mensagem);
        invitePostRequestBody.setRequireSignIn(true);
        invitePostRequestBody.setSendInvitation(true);

        invitePostRequestBody.setRoles(List.of("write"));
        invitePostRequestBody.setExpirationDateTime(LocalDateTime.now()
                .plusDays(EXPIRATION_DAYS)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.000'Z'")));

        graphServiceClient
                .drives()
                .byDriveId(driveId)
                .items()
                .byDriveItemId(driveItemId)
                .invite()
                .post(invitePostRequestBody);
    }

    public void remove(final String driveId, final String driveItemId, final String emailUsuario) {
        final PermissionCollectionResponse permissions = graphServiceClient
                .drives()
                .byDriveId(driveId)
                .items()
                .byDriveItemId(driveItemId)
                .permissions()
                .get();

        final String permissionId = permissions.getValue().stream()
                .filter(i -> Objects.nonNull(i.getGrantedToV2()))
                .filter(i -> Objects.nonNull(i.getGrantedToV2().getUser()))
                .filter(i -> i.getGrantedToV2().getUser().getAdditionalData().get("email").equals(emailUsuario))
                .findFirst()
                .map(Entity::getId)
                .orElseThrow(() -> new RuntimeException("User does not have permission on the item."));

        graphServiceClient
                .drives()
                .byDriveId(driveId)
                .items()
                .byDriveItemId(driveItemId)
                .permissions()
                .byPermissionId(permissionId)
                .delete();
    }
}
