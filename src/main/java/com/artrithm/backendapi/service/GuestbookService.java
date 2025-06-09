package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.GuestbookDto;
import com.artrithm.backendapi.model.Exhibition;
import com.artrithm.backendapi.model.Guestbook;
import com.artrithm.backendapi.model.User;
import com.artrithm.backendapi.repository.ExhibitionRepository;
import com.artrithm.backendapi.repository.GuestbookRepository;
import com.artrithm.backendapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestbookService {

    private final GuestbookRepository guestbookRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final UserRepository userRepository;

    // ✅ 전시 방명록 전체 조회
    public List<GuestbookDto> getAll(Long exhibitionId) {
        return guestbookRepository.findByExhibitionIdOrderByCreatedAtDesc(exhibitionId)
                .stream()
                .map(GuestbookDto::from)
                .collect(Collectors.toList());
    }

    // ✅ 방명록 작성
    public void write(Long exhibitionId, GuestbookDto dto) {
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new NoSuchElementException("전시를 찾을 수 없습니다."));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        Guestbook guestbook = new Guestbook();
        guestbook.setExhibition(exhibition);
        guestbook.setUser(user);
        guestbook.setContent(dto.getContent());

        guestbookRepository.save(guestbook);
    }

    // ✅ 방명록 수정
    public void update(Long guestbookId, GuestbookDto dto) {
        Guestbook guestbook = guestbookRepository.findById(guestbookId)
                .orElseThrow(() -> new NoSuchElementException("방명록이 존재하지 않습니다."));

        if (!guestbook.getUser().getId().equals(dto.getUserId())) {
            throw new SecurityException("본인의 방명록만 수정할 수 있습니다.");
        }

        guestbook.setContent(dto.getContent());
        guestbookRepository.save(guestbook);
    }

    // ✅ 방명록 삭제
    public void delete(Long guestbookId, Long userId) {
        Guestbook guestbook = guestbookRepository.findById(guestbookId)
                .orElseThrow(() -> new NoSuchElementException("방명록이 존재하지 않습니다."));

        if (!guestbook.getUser().getId().equals(userId)) {
            throw new SecurityException("본인의 방명록만 삭제할 수 있습니다.");
        }

        guestbookRepository.delete(guestbook);
    }
}
