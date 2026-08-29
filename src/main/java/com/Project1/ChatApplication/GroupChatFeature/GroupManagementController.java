package com.Project1.ChatApplication.GroupChatFeature;

import com.Project1.ChatApplication.GroupChatFeature.DtoClasses.GroupInfoResponseDto;
import com.Project1.ChatApplication.GroupChatFeature.DtoClasses.GroupMemberInfoResponseDto;
import com.Project1.ChatApplication.GroupChatFeature.DtoClasses.RequestDto;
import com.Project1.ChatApplication.GroupChatFeature.DtoClasses.UpdatedGroupInfoDto;
import com.Project1.ChatApplication.WrapperClasses.ApiWrapperClass;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "${app.cors.allowed-origins}")
@RestController
public class GroupManagementController {
    @Autowired
    private GroupManagementService groupManagementService;
    @PostMapping("/createNewGroup")
    public ResponseEntity<ApiWrapperClass<Void>> createNewGroup(@RequestBody RequestDto groupInfo){
        return groupManagementService.createNewGroup(groupInfo);
    }
    @GetMapping("/getAllGroupDetails")
    public  ResponseEntity<ApiWrapperClass<List<GroupInfoResponseDto>>> getAllGroupInfo(){


        return groupManagementService.getAllGroupsDetails();
    }
    @DeleteMapping("/deleteGroupMember")
    public ResponseEntity<ApiWrapperClass<Void>> deleteGroupMember(@RequestBody RequestDto deletingGroupID){
        return  groupManagementService.deleteGroupMember(deletingGroupID);
    }
    @GetMapping("/getAllGroupMembers/{groupID}")
    public  ResponseEntity<ApiWrapperClass<List<GroupMemberInfoResponseDto>>> getAllGroupMembers(@PathVariable String groupID){
        return groupManagementService.getAllGroupMembers(groupID);
    }
   @PutMapping("/updateGroupInfo")
    public ResponseEntity<ApiWrapperClass<Void>> updateGroupInfo(@RequestBody UpdatedGroupInfoDto updatedGroupInfo){
        return  groupManagementService.updateGroupInfo(updatedGroupInfo);
   }
}
