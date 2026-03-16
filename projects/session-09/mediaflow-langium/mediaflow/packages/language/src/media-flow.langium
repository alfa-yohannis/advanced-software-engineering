grammar MediaFlow

hidden terminal WS: /\s+/;
hidden terminal ML_COMMENT: /\/\*[\s\S]*?\*\//;
hidden terminal SL_COMMENT: /\/\/[^\n\r]*/;

terminal ID: /[_a-zA-Z][\w_]*/;
terminal INT returns number: /[0-9]+/;
terminal STRING: /"(\\.|[^"\\])*"|'(\\.|[^'\\])*'/;

entry Model:
    elements+=Element*;

Element:
    Graph
;

Graph:
    'graph' name=ID '{'
        elements+=GraphElement*
    '}'
;

GraphElement:
    Node | Edge
;

Node:
    Resource | Scaler | Transcoder
;

Resource:
    'resource' name=ID
    ('[' 'mediaType' '=' mediaType=MediaType ']')?
    'uri' '=' uri=STRING
    ('external' '=' external=BooleanLiteral)?
    '{'
        ports+=Port*
    '}'
;

Scaler:
    'scaler' name=ID
    'backend' '=' backend=Backend
    'command' '=' command=STRING
    'replicas' '=' replicas=INT
    'width' '=' width=INT
    'height' '=' height=INT
    '{'
        ports+=Port*
    '}'
;

Transcoder:
    'transcoder' name=ID
    'backend' '=' backend=Backend
    'command' '=' command=STRING
    'replicas' '=' replicas=INT
    'videoCodec' '=' videoCodec=STRING
    'audioCodec' '=' audioCodec=STRING
    'container' '=' container=STRING
    'bitrate' '=' bitrate=INT
    '{'
        ports+=Port*
    '}'
;

Port:
    'port' name=ID direction=Direction
;

Edge:
    'edge' name=ID
    'from' source=[Port:QualifiedPortName]
    'to' target=[Port:QualifiedPortName]
;

QualifiedPortName returns string:
    ID ('.' ID)?
;

Direction returns string:
    'IN' | 'OUT'
;

Backend returns string:
    'FFMPEG' | 'GSTREAMER' | 'CUSTOM'
;

MediaType returns string:
    'VIDEO' | 'AUDIO' | 'SUBTITLE' | 'IMAGE' | 'IMAGE_SEQUENCE'
;

BooleanLiteral returns boolean:
    'true' | 'false'
;
