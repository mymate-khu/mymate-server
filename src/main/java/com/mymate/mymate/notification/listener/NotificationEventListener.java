package com.mymate.mymate.notification.listener;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.mymate.mymate.group.repository.GroupMemberRepository;
import com.mymate.mymate.notification.event.ChatMessageEvent;
import com.mymate.mymate.notification.event.GroupInvitationEvent;
import com.mymate.mymate.notification.event.GroupMemberJoinedEvent;
import com.mymate.mymate.notification.event.PuzzleCreatedEvent;
import com.mymate.mymate.notification.event.PuzzleDueSoonEvent;
import com.mymate.mymate.notification.event.SettlementCreatedEvent;
import com.mymate.mymate.notification.event.SettlementUpdatedEvent;
import com.mymate.mymate.notification.event.SettlementCompletedEvent;
import com.mymate.mymate.notification.service.NotificationComposerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 알림 이벤트 리스너
 * 비동기로 알림을 처리하여 메인 트랜잭션에 영향을 주지 않음
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {
    
    private final NotificationComposerService composerService;
    private final GroupMemberRepository groupMemberRepository;
    
    /**
     * 그룹 초대 이벤트 처리
     */
    @EventListener
    @Async("notificationTaskExecutor")
    public void handleGroupInvitation(GroupInvitationEvent event) {
        try {
            composerService.composeAndSend(
                event.getRecipientId(), 
                event.getSenderId(), 
                event.getType(), 
                event.getTitle(), 
                event.getContent(), 
                event.getPriority(), 
                event.getNavigationUrl(), 
                event.getData()
            );
            log.info("그룹 초대 알림 발송 완료: inviteeId={}, groupName={}", 
                    event.getRecipientId(), event.getGroupName());
        } catch (Exception e) {
            log.error("그룹 초대 알림 발송 실패: inviteeId={}, groupName={}, error={}", 
                     event.getRecipientId(), event.getGroupName(), e.getMessage());
        }
    }
    
    /**
     * 그룹 멤버 가입 이벤트 처리
     */
    @EventListener
    @Async("notificationTaskExecutor")
    public void handleGroupMemberJoined(GroupMemberJoinedEvent event) {
        try {
            // 그룹 멤버들에게 알림 발송 (새로 가입한 멤버 제외)
            List<Long> memberIds = groupMemberRepository.findMemberIdsByGroupId(
                (Long) event.getData().get("groupId")
            );
            
             for (Long memberId : memberIds) {
                 if (!memberId.equals(event.getSenderId())) { // 새로 가입한 멤버 제외
                     composerService.composeAndSend(
                         memberId, 
                         event.getSenderId(), 
                         event.getType(), 
                         event.getTitle(), 
                         event.getContent(), 
                         event.getPriority(), 
                         event.getNavigationUrl(), 
                         event.getData()
                     );
                 }
             }
            log.info("그룹 멤버 가입 알림 발송 완료: groupName={}, memberName={}", 
                    event.getGroupName(), event.getMemberName());
        } catch (Exception e) {
            log.error("그룹 멤버 가입 알림 발송 실패: groupName={}, memberName={}, error={}", 
                     event.getGroupName(), event.getMemberName(), e.getMessage());
        }
    }
    
    /**
     * 퍼즐 생성 이벤트 처리
     */
    @EventListener
    @Async("notificationTaskExecutor")
    public void handlePuzzleCreated(PuzzleCreatedEvent event) {
        try {
            // 그룹 멤버들에게 알림 발송 (생성자 제외)
            List<Long> memberIds = groupMemberRepository.findMemberIdsByGroupId(event.getGroupId());
            
             for (Long memberId : memberIds) {
                 if (!memberId.equals(event.getSenderId())) { // 생성자 제외
                     composerService.composeAndSend(
                         memberId, 
                         event.getSenderId(), 
                         event.getType(), 
                         event.getTitle(), 
                         event.getContent(), 
                         event.getPriority(), 
                         event.getNavigationUrl(), 
                         event.getData()
                     );
                 }
             }
            log.info("퍼즐 생성 알림 발송 완료: groupId={}, puzzleTitle={}", 
                    event.getGroupId(), event.getPuzzleTitle());
        } catch (Exception e) {
            log.error("퍼즐 생성 알림 발송 실패: groupId={}, puzzleTitle={}, error={}", 
                     event.getGroupId(), event.getPuzzleTitle(), e.getMessage());
        }
    }
    
    /**
     * 퍼즐 마감 임박 이벤트 처리
     */
    @EventListener
    @Async("notificationTaskExecutor")
    public void handlePuzzleDueSoon(PuzzleDueSoonEvent event) {
        try {
            // 그룹 멤버들에게 알림 발송
            List<Long> memberIds = groupMemberRepository.findMemberIdsByGroupId(event.getGroupId());
            
            for (Long memberId : memberIds) {
                composerService.composeAndSend(
                    memberId, 
                    null, 
                    event.getType(), 
                    event.getTitle(), 
                    event.getContent(), 
                    event.getPriority(), 
                    event.getNavigationUrl(), 
                    event.getData()
                );
            }
            log.info("퍼즐 마감 임박 알림 발송 완료: groupId={}, puzzleTitle={}", 
                    event.getGroupId(), event.getPuzzleTitle());
        } catch (Exception e) {
            log.error("퍼즐 마감 임박 알림 발송 실패: groupId={}, puzzleTitle={}, error={}", 
                     event.getGroupId(), event.getPuzzleTitle(), e.getMessage());
        }
    }
    
    /**
     * 채팅 메시지 이벤트 처리
     */
    @EventListener
    @Async("notificationTaskExecutor")
    public void handleChatMessage(ChatMessageEvent event) {
        try {
            // 그룹 멤버들에게 알림 발송 (발송자 제외)
            List<Long> memberIds = groupMemberRepository.findMemberIdsByGroupId(event.getGroupId());
            
             for (Long memberId : memberIds) {
                 if (!memberId.equals(event.getSenderId())) { // 발송자 제외
                     composerService.composeAndSend(
                         memberId, 
                         event.getSenderId(), 
                         event.getType(), 
                         event.getTitle(), 
                         event.getContent(), 
                         event.getPriority(), 
                         event.getNavigationUrl(), 
                         event.getData()
                     );
                 }
             }
            log.info("채팅 메시지 알림 발송 완료: groupId={}, senderName={}", 
                    event.getGroupId(), event.getSenderName());
        } catch (Exception e) {
             log.error("채팅 메시지 알림 발송 실패: groupId={}, senderName={}, error={}", 
                      event.getGroupId(), event.getSenderName(), e.getMessage());
         }
     }
     
     /**
      * 정산 생성 이벤트 처리
      */
     @EventListener
     @Async("notificationTaskExecutor")
     public void handleSettlementCreated(SettlementCreatedEvent event) {
         try {
             // 그룹 멤버들에게 알림 발송 (생성자 제외)
             List<Long> memberIds = groupMemberRepository.findMemberIdsByGroupId(event.getGroupId());
             
             for (Long memberId : memberIds) {
                 if (!memberId.equals(event.getSenderId())) { // 생성자 제외
                     composerService.composeAndSend(
                         memberId, 
                         event.getSenderId(), 
                         event.getType(), 
                         event.getTitle(), 
                         event.getContent(), 
                         event.getPriority(), 
                         event.getNavigationUrl(), 
                         event.getData()
                     );
                 }
             }
             log.info("정산 생성 알림 발송 완료: groupId={}, settlementTitle={}", 
                     event.getGroupId(), event.getSettlementTitle());
         } catch (Exception e) {
             log.error("정산 생성 알림 발송 실패: groupId={}, settlementTitle={}, error={}", 
                      event.getGroupId(), event.getSettlementTitle(), e.getMessage());
         }
     }
     
     /**
      * 정산 업데이트 이벤트 처리
      */
     @EventListener
     @Async("notificationTaskExecutor")
     public void handleSettlementUpdated(SettlementUpdatedEvent event) {
         try {
             // 그룹 멤버들에게 알림 발송 (업데이트자 제외)
             List<Long> memberIds = groupMemberRepository.findMemberIdsByGroupId(event.getGroupId());
             
             for (Long memberId : memberIds) {
                 if (!memberId.equals(event.getSenderId())) { // 업데이트자 제외
                     composerService.composeAndSend(
                         memberId, 
                         event.getSenderId(), 
                         event.getType(), 
                         event.getTitle(), 
                         event.getContent(), 
                         event.getPriority(), 
                         event.getNavigationUrl(), 
                         event.getData()
                     );
                 }
             }
             log.info("정산 업데이트 알림 발송 완료: groupId={}, settlementTitle={}", 
                     event.getGroupId(), event.getSettlementTitle());
         } catch (Exception e) {
             log.error("정산 업데이트 알림 발송 실패: groupId={}, settlementTitle={}, error={}", 
                      event.getGroupId(), event.getSettlementTitle(), e.getMessage());
         }
     }
     
     /**
      * 정산 완료 이벤트 처리
      */
     @EventListener
     @Async("notificationTaskExecutor")
     public void handleSettlementCompleted(SettlementCompletedEvent event) {
         try {
             // 그룹 멤버들에게 알림 발송 (완료자 제외)
             List<Long> memberIds = groupMemberRepository.findMemberIdsByGroupId(event.getGroupId());
             
             for (Long memberId : memberIds) {
                 if (!memberId.equals(event.getSenderId())) { // 완료자 제외
                     composerService.composeAndSend(
                         memberId, 
                         event.getSenderId(), 
                         event.getType(), 
                         event.getTitle(), 
                         event.getContent(), 
                         event.getPriority(), 
                         event.getNavigationUrl(), 
                         event.getData()
                     );
                 }
             }
             log.info("정산 완료 알림 발송 완료: groupId={}, settlementTitle={}", 
                     event.getGroupId(), event.getSettlementTitle());
         } catch (Exception e) {
             log.error("정산 완료 알림 발송 실패: groupId={}, settlementTitle={}, error={}", 
                      event.getGroupId(), event.getSettlementTitle(), e.getMessage());
         }
     }
}
