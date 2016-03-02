package com.softserve.edu.specification;

import com.softserve.edu.entity.catalogue.Team.DisassemblyTeam;
import com.softserve.edu.entity.device.Device;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Expression;
import java.util.Collection;
import java.util.Date;

/**
 * Created for filtering data using PagingAndSortingRepository
 */
public class CalibrationDisassenblyTeamSpecifications {

    /**
     * build query for id, filtering by id
     *
     * @param id
     * @return query, for searching
     */
    public static Specification<DisassemblyTeam> disassemblyTeamHasId(String id) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), id);
    }

    /**
     * build query for name, filtering by name
     *
     * @param name
     * @return query, for searching
     */
    public static Specification<DisassemblyTeam> disassemblyTeamHasName(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), name);
    }

    /**
     * build query for effectiveTo, filtering by effective
     *
     * @param effectiveTo
     * @return query, for searching
     */
    public static Specification<DisassemblyTeam> disassemblyTeamHasEffectiveTo(Date effectiveTo) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("effectiveTo"), effectiveTo);
    }

    /**
     * build query for disassemblyTeamType, filtering by disassemblyTeamType
     *
     * @param disassemblyTeamType
     * @return query, for searching
     */
    public static Specification<DisassemblyTeam> disassemblyTeamHasType(Device.DeviceType disassemblyTeamType) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.isMember(disassemblyTeamType, root.get("specialization"));
    }

    /**
     * build query for leaderFullName, filtering by leaderFullName
     *
     * @param leaderFullName
     * @return query, for searching
     */
    public static Specification<DisassemblyTeam> disassemblyTeamHasLeaderFullName(String leaderFullName) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("leaderFullName"), leaderFullName);
    }

    /**
     * build query for leaderPhone, filtering by leaderPhone
     *
     * @param leaderPhone
     * @return query, for searching
     */
    public static Specification<DisassemblyTeam> disassemblyTeamHasLeaderPhone(String leaderPhone) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("leaderPhone"), leaderPhone);
    }

    /**
     * build query for leaderEmail, filtering by leaderEmail
     *
     * @param leaderEmail
     * @return query, for searching
     */
    public static Specification<DisassemblyTeam> disassemblyTeamHasLeaderEmail(String leaderEmail) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("leaderEmail"), leaderEmail);
    }

    public static Specification<DisassemblyTeam> disassemblyTeamHasCalibratorId(Long calibratorId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("organization").get("id"), calibratorId);
    }


}
