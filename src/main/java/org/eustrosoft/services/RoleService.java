package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.entitites.Role;
import org.eustrosoft.entitites.enums.Roles;
import org.eustrosoft.repositories.RoleRepository;
import org.eustrosoft.utils.CommonUtils;
import org.eustrosoft.utils.JwtTokenUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final JwtTokenUtils jwtTokenUtils;

    public List<Role> findRoles() {
        return CommonUtils.iterableToList(roleRepository.findAll());
    }

    public Role findRoleById(Long id) {
        return roleRepository.findById(id).get();
    }

    public List<Role> getRolesByName(Roles role) {
        return Collections.singletonList(roleRepository.findByName(role.getName()).get());
    }

    public List<String> getRolesByToken() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();
        return jwtTokenUtils.getRoles(request.getHeader("Authorization").substring(7));
    }
}
