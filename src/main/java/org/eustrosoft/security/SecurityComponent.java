package org.eustrosoft.security;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.services.ParticipantService;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class SecurityComponent {
    private final ParticipantService participantService;

    public void checkUserRightById(Supplier<Long> participantIdSupplier)
            throws IllegalArgumentException, IllegalAccessException {
        if (participantIdSupplier == null) {
            throw new IllegalArgumentException("Supplier not provided");
        }
        Long participantId = participantIdSupplier.get();
        if (participantId == null) {
            throw new IllegalAccessException("Participant not found for this qr");
        }
        Participant current = participantService.getCurrentOrThrow();
        if (!Objects.equals(current.getId(), participantId)) {
            throw new IllegalAccessException("You are not the owner of this object");
        }
    }

}
