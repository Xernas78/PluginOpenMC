package fr.communaywen.core.utils.constant;


import fr.communaywen.core.credit.annotations.Credit;
import fr.communaywen.core.credit.annotations.Feature;

@Feature("beautiful-message")
@Credit("Axeno")
public enum MessageType {
    
    ERROR,
    WARNING,
    SUCCESS,
    INFO,

    ;

}
