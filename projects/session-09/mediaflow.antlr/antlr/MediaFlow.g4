grammar MediaFlow;

model  
    : graph EOF
    ; 

graph
    : 'graph' name=ID '{'
        node* 
        edge* 
      '}'
    ;

node
    : resource
    | scaler
    | transcoder 
    ;

resource
    : 'resource' name=ID
      '[' 'mediaType' '=' mediaTypeValue=mediaType ']'
      'uri' '=' uri=STRING
      'external' '=' externalValue=booleanLiteral
      '{'
        port*
      '}'
    ;

scaler
    : 'scaler' name=ID
      'backend' '=' backendValue=backend
      'command' '=' command=STRING
      'replicas' '=' replicas=INT
      'width' '=' width=INT
      'height' '=' height=INT
      '{'
        port*
      '}'
    ;

transcoder
    : 'transcoder' name=ID
      'backend' '=' backendValue=backend
      'command' '=' command=STRING
      'replicas' '=' replicas=INT
      'videoCodec' '=' videoCodec=STRING
      'audioCodec' '=' audioCodec=STRING
      'container' '=' container=STRING
      'bitrate' '=' bitrate=INT
      '{'
        port*
      '}'
    ;

port
    : 'port' name=ID directionValue=direction
    ;

edge
    : 'edge' name=ID
      'from' source=ID
      'to' target=ID
    ;

mediaType
    : 'VIDEO'
    | 'AUDIO'
    | 'SUBTITLE'
    | 'IMAGE'
    | 'IMAGE_SEQUENCE'
    ;

backend
    : 'FFMPEG'
    | 'GSTREAMER'
    | 'CUSTOM'
    ;

direction
    : 'IN'
    | 'OUT'
    ;

booleanLiteral
    : 'true'
    | 'false'
    ;

ID      : [a-zA-Z_][a-zA-Z_0-9]* ;
INT     : [0-9]+ ;
STRING  : '"' ( ~["\\] | '\\' . )* '"' ;
WS      : [ \t\r\n]+ -> skip ;
COMMENT : '//' ~[\r\n]* -> skip ;