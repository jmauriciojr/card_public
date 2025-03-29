//package br.com.hyperativa.card.model;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import jakarta.persistence.CascadeType;
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.OneToMany;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "file_data")
//public class FileData {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    // Dados do Header
//    private String nome;
//    private String data;
//    
//    @Column(name = "lot_header")
//    private String lotHeader;
//    
//    @Column(name = "quantity_records_header")
//    private String quantityRecordsHeader;
//
//    @Column(name = "lot_trailer")
//    private String lotTrailer;
//    
//    @Column(name = "quantity_records_trailer")
//    private String quantityRecordsTrailer;
//
//    @OneToMany(mappedBy = "fileData", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<CardRecord> records = new ArrayList<>();
//
//}
