//package br.com.hyperativa.card.model;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "card_record")
//public class CardRecord {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "line_identification")
//    private String lineIdentification;
//   
//    @Column(name = "lot_identification")
//    private String lotIdentification;
//
//    @Column(name = "card_number")
//    private String cardNumber;
//
//    @ManyToOne
//    @JoinColumn(name = "file_data_id")
//    private FileData fileData;
//}
