package com.portafolio.bidding.infrastructure.adapter;

import com.portafolio.bidding.domain.entity.Bid;
import com.portafolio.bidding.domain.repository.BidRepository;
import com.portafolio.bidding.infrastructure.mapper.BidMapper;
import com.portafolio.bidding.infrastructure.repository.JpaBidRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class BidRepositoryAdapter implements BidRepository {

    private final JpaBidRepository jpaRepository;

    public BidRepositoryAdapter(JpaBidRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Bid save(Bid bid) {
        var entity = BidMapper.toEntity(bid);
        var savedEntity = jpaRepository.save(entity);
        return BidMapper.toDomain(savedEntity);
    }

    @Override
    public List<Bid> findByAuctionId(Long auctionId) {
        return jpaRepository.findByAuctionIdOrderByAmountDesc(auctionId).stream()
                .map(BidMapper::toDomain)
                .collect(Collectors.toList());
    }
}