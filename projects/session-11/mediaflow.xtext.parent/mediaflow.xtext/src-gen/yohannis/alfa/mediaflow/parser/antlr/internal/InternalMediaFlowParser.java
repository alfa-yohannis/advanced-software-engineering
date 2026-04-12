package yohannis.alfa.mediaflow.parser.antlr.internal;

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import yohannis.alfa.mediaflow.services.MediaFlowGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalMediaFlowParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_ID", "RULE_STRING", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'graph'", "'{'", "'}'", "'resource'", "'['", "'mediaType'", "'='", "']'", "'uri'", "'external'", "'scaler'", "'backend'", "'command'", "'replicas'", "'width'", "'height'", "'transcoder'", "'videoCodec'", "'audioCodec'", "'container'", "'bitrate'", "'port'", "'edge'", "'from'", "'to'", "'true'", "'false'", "'VIDEO'", "'AUDIO'", "'SUBTITLE'", "'IMAGE'", "'IMAGE_SEQUENCE'", "'FFMPEG'", "'GSTREAMER'", "'CUSTOM'", "'IN'", "'OUT'"
    };
    public static final int T__19=19;
    public static final int T__15=15;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__11=11;
    public static final int T__12=12;
    public static final int T__13=13;
    public static final int T__14=14;
    public static final int RULE_ID=4;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int RULE_INT=6;
    public static final int T__29=29;
    public static final int T__22=22;
    public static final int RULE_ML_COMMENT=7;
    public static final int T__23=23;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__20=20;
    public static final int T__21=21;
    public static final int RULE_STRING=5;
    public static final int RULE_SL_COMMENT=8;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int EOF=-1;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int RULE_WS=9;
    public static final int RULE_ANY_OTHER=10;
    public static final int T__44=44;
    public static final int T__45=45;
    public static final int T__46=46;
    public static final int T__47=47;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int T__42=42;
    public static final int T__43=43;

    // delegates
    // delegators


        public InternalMediaFlowParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalMediaFlowParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalMediaFlowParser.tokenNames; }
    public String getGrammarFileName() { return "InternalMediaFlow.g"; }



     	private MediaFlowGrammarAccess grammarAccess;

        public InternalMediaFlowParser(TokenStream input, MediaFlowGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }

        @Override
        protected String getFirstRuleName() {
        	return "Model";
       	}

       	@Override
       	protected MediaFlowGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}




    // $ANTLR start "entryRuleModel"
    // InternalMediaFlow.g:65:1: entryRuleModel returns [EObject current=null] : iv_ruleModel= ruleModel EOF ;
    public final EObject entryRuleModel() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleModel = null;


        try {
            // InternalMediaFlow.g:65:46: (iv_ruleModel= ruleModel EOF )
            // InternalMediaFlow.g:66:2: iv_ruleModel= ruleModel EOF
            {
             newCompositeNode(grammarAccess.getModelRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleModel=ruleModel();

            state._fsp--;

             current =iv_ruleModel; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleModel"


    // $ANTLR start "ruleModel"
    // InternalMediaFlow.g:72:1: ruleModel returns [EObject current=null] : this_Graph_0= ruleGraph ;
    public final EObject ruleModel() throws RecognitionException {
        EObject current = null;

        EObject this_Graph_0 = null;



        	enterRule();

        try {
            // InternalMediaFlow.g:78:2: (this_Graph_0= ruleGraph )
            // InternalMediaFlow.g:79:2: this_Graph_0= ruleGraph
            {

            		newCompositeNode(grammarAccess.getModelAccess().getGraphParserRuleCall());
            	
            pushFollow(FOLLOW_2);
            this_Graph_0=ruleGraph();

            state._fsp--;


            		current = this_Graph_0;
            		afterParserOrEnumRuleCall();
            	

            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleModel"


    // $ANTLR start "entryRuleGraph"
    // InternalMediaFlow.g:90:1: entryRuleGraph returns [EObject current=null] : iv_ruleGraph= ruleGraph EOF ;
    public final EObject entryRuleGraph() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleGraph = null;


        try {
            // InternalMediaFlow.g:90:46: (iv_ruleGraph= ruleGraph EOF )
            // InternalMediaFlow.g:91:2: iv_ruleGraph= ruleGraph EOF
            {
             newCompositeNode(grammarAccess.getGraphRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleGraph=ruleGraph();

            state._fsp--;

             current =iv_ruleGraph; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleGraph"


    // $ANTLR start "ruleGraph"
    // InternalMediaFlow.g:97:1: ruleGraph returns [EObject current=null] : (otherlv_0= 'graph' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '{' ( (lv_nodes_3_0= ruleNode ) )* ( (lv_edges_4_0= ruleEdge ) )* otherlv_5= '}' ) ;
    public final EObject ruleGraph() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_5=null;
        EObject lv_nodes_3_0 = null;

        EObject lv_edges_4_0 = null;



        	enterRule();

        try {
            // InternalMediaFlow.g:103:2: ( (otherlv_0= 'graph' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '{' ( (lv_nodes_3_0= ruleNode ) )* ( (lv_edges_4_0= ruleEdge ) )* otherlv_5= '}' ) )
            // InternalMediaFlow.g:104:2: (otherlv_0= 'graph' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '{' ( (lv_nodes_3_0= ruleNode ) )* ( (lv_edges_4_0= ruleEdge ) )* otherlv_5= '}' )
            {
            // InternalMediaFlow.g:104:2: (otherlv_0= 'graph' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '{' ( (lv_nodes_3_0= ruleNode ) )* ( (lv_edges_4_0= ruleEdge ) )* otherlv_5= '}' )
            // InternalMediaFlow.g:105:3: otherlv_0= 'graph' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '{' ( (lv_nodes_3_0= ruleNode ) )* ( (lv_edges_4_0= ruleEdge ) )* otherlv_5= '}'
            {
            otherlv_0=(Token)match(input,11,FOLLOW_3); 

            			newLeafNode(otherlv_0, grammarAccess.getGraphAccess().getGraphKeyword_0());
            		
            // InternalMediaFlow.g:109:3: ( (lv_name_1_0= RULE_ID ) )
            // InternalMediaFlow.g:110:4: (lv_name_1_0= RULE_ID )
            {
            // InternalMediaFlow.g:110:4: (lv_name_1_0= RULE_ID )
            // InternalMediaFlow.g:111:5: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_4); 

            					newLeafNode(lv_name_1_0, grammarAccess.getGraphAccess().getNameIDTerminalRuleCall_1_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getGraphRule());
            					}
            					setWithLastConsumed(
            						current,
            						"name",
            						lv_name_1_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }

            otherlv_2=(Token)match(input,12,FOLLOW_5); 

            			newLeafNode(otherlv_2, grammarAccess.getGraphAccess().getLeftCurlyBracketKeyword_2());
            		
            // InternalMediaFlow.g:131:3: ( (lv_nodes_3_0= ruleNode ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==14||LA1_0==21||LA1_0==27) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // InternalMediaFlow.g:132:4: (lv_nodes_3_0= ruleNode )
            	    {
            	    // InternalMediaFlow.g:132:4: (lv_nodes_3_0= ruleNode )
            	    // InternalMediaFlow.g:133:5: lv_nodes_3_0= ruleNode
            	    {

            	    					newCompositeNode(grammarAccess.getGraphAccess().getNodesNodeParserRuleCall_3_0());
            	    				
            	    pushFollow(FOLLOW_5);
            	    lv_nodes_3_0=ruleNode();

            	    state._fsp--;


            	    					if (current==null) {
            	    						current = createModelElementForParent(grammarAccess.getGraphRule());
            	    					}
            	    					add(
            	    						current,
            	    						"nodes",
            	    						lv_nodes_3_0,
            	    						"yohannis.alfa.mediaflow.MediaFlow.Node");
            	    					afterParserOrEnumRuleCall();
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            // InternalMediaFlow.g:150:3: ( (lv_edges_4_0= ruleEdge ) )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==33) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // InternalMediaFlow.g:151:4: (lv_edges_4_0= ruleEdge )
            	    {
            	    // InternalMediaFlow.g:151:4: (lv_edges_4_0= ruleEdge )
            	    // InternalMediaFlow.g:152:5: lv_edges_4_0= ruleEdge
            	    {

            	    					newCompositeNode(grammarAccess.getGraphAccess().getEdgesEdgeParserRuleCall_4_0());
            	    				
            	    pushFollow(FOLLOW_6);
            	    lv_edges_4_0=ruleEdge();

            	    state._fsp--;


            	    					if (current==null) {
            	    						current = createModelElementForParent(grammarAccess.getGraphRule());
            	    					}
            	    					add(
            	    						current,
            	    						"edges",
            	    						lv_edges_4_0,
            	    						"yohannis.alfa.mediaflow.MediaFlow.Edge");
            	    					afterParserOrEnumRuleCall();
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            otherlv_5=(Token)match(input,13,FOLLOW_2); 

            			newLeafNode(otherlv_5, grammarAccess.getGraphAccess().getRightCurlyBracketKeyword_5());
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleGraph"


    // $ANTLR start "entryRuleNode"
    // InternalMediaFlow.g:177:1: entryRuleNode returns [EObject current=null] : iv_ruleNode= ruleNode EOF ;
    public final EObject entryRuleNode() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNode = null;


        try {
            // InternalMediaFlow.g:177:45: (iv_ruleNode= ruleNode EOF )
            // InternalMediaFlow.g:178:2: iv_ruleNode= ruleNode EOF
            {
             newCompositeNode(grammarAccess.getNodeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleNode=ruleNode();

            state._fsp--;

             current =iv_ruleNode; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleNode"


    // $ANTLR start "ruleNode"
    // InternalMediaFlow.g:184:1: ruleNode returns [EObject current=null] : (this_Resource_0= ruleResource | this_Transformer_1= ruleTransformer ) ;
    public final EObject ruleNode() throws RecognitionException {
        EObject current = null;

        EObject this_Resource_0 = null;

        EObject this_Transformer_1 = null;



        	enterRule();

        try {
            // InternalMediaFlow.g:190:2: ( (this_Resource_0= ruleResource | this_Transformer_1= ruleTransformer ) )
            // InternalMediaFlow.g:191:2: (this_Resource_0= ruleResource | this_Transformer_1= ruleTransformer )
            {
            // InternalMediaFlow.g:191:2: (this_Resource_0= ruleResource | this_Transformer_1= ruleTransformer )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==14) ) {
                alt3=1;
            }
            else if ( (LA3_0==21||LA3_0==27) ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // InternalMediaFlow.g:192:3: this_Resource_0= ruleResource
                    {

                    			newCompositeNode(grammarAccess.getNodeAccess().getResourceParserRuleCall_0());
                    		
                    pushFollow(FOLLOW_2);
                    this_Resource_0=ruleResource();

                    state._fsp--;


                    			current = this_Resource_0;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 2 :
                    // InternalMediaFlow.g:201:3: this_Transformer_1= ruleTransformer
                    {

                    			newCompositeNode(grammarAccess.getNodeAccess().getTransformerParserRuleCall_1());
                    		
                    pushFollow(FOLLOW_2);
                    this_Transformer_1=ruleTransformer();

                    state._fsp--;


                    			current = this_Transformer_1;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleNode"


    // $ANTLR start "entryRuleTransformer"
    // InternalMediaFlow.g:213:1: entryRuleTransformer returns [EObject current=null] : iv_ruleTransformer= ruleTransformer EOF ;
    public final EObject entryRuleTransformer() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTransformer = null;


        try {
            // InternalMediaFlow.g:213:52: (iv_ruleTransformer= ruleTransformer EOF )
            // InternalMediaFlow.g:214:2: iv_ruleTransformer= ruleTransformer EOF
            {
             newCompositeNode(grammarAccess.getTransformerRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTransformer=ruleTransformer();

            state._fsp--;

             current =iv_ruleTransformer; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTransformer"


    // $ANTLR start "ruleTransformer"
    // InternalMediaFlow.g:220:1: ruleTransformer returns [EObject current=null] : (this_Scaler_0= ruleScaler | this_Transcoder_1= ruleTranscoder ) ;
    public final EObject ruleTransformer() throws RecognitionException {
        EObject current = null;

        EObject this_Scaler_0 = null;

        EObject this_Transcoder_1 = null;



        	enterRule();

        try {
            // InternalMediaFlow.g:226:2: ( (this_Scaler_0= ruleScaler | this_Transcoder_1= ruleTranscoder ) )
            // InternalMediaFlow.g:227:2: (this_Scaler_0= ruleScaler | this_Transcoder_1= ruleTranscoder )
            {
            // InternalMediaFlow.g:227:2: (this_Scaler_0= ruleScaler | this_Transcoder_1= ruleTranscoder )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==21) ) {
                alt4=1;
            }
            else if ( (LA4_0==27) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // InternalMediaFlow.g:228:3: this_Scaler_0= ruleScaler
                    {

                    			newCompositeNode(grammarAccess.getTransformerAccess().getScalerParserRuleCall_0());
                    		
                    pushFollow(FOLLOW_2);
                    this_Scaler_0=ruleScaler();

                    state._fsp--;


                    			current = this_Scaler_0;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 2 :
                    // InternalMediaFlow.g:237:3: this_Transcoder_1= ruleTranscoder
                    {

                    			newCompositeNode(grammarAccess.getTransformerAccess().getTranscoderParserRuleCall_1());
                    		
                    pushFollow(FOLLOW_2);
                    this_Transcoder_1=ruleTranscoder();

                    state._fsp--;


                    			current = this_Transcoder_1;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTransformer"


    // $ANTLR start "entryRuleResource"
    // InternalMediaFlow.g:249:1: entryRuleResource returns [EObject current=null] : iv_ruleResource= ruleResource EOF ;
    public final EObject entryRuleResource() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleResource = null;


        try {
            // InternalMediaFlow.g:249:49: (iv_ruleResource= ruleResource EOF )
            // InternalMediaFlow.g:250:2: iv_ruleResource= ruleResource EOF
            {
             newCompositeNode(grammarAccess.getResourceRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleResource=ruleResource();

            state._fsp--;

             current =iv_ruleResource; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleResource"


    // $ANTLR start "ruleResource"
    // InternalMediaFlow.g:256:1: ruleResource returns [EObject current=null] : (otherlv_0= 'resource' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '[' otherlv_3= 'mediaType' otherlv_4= '=' ( (lv_mediaType_5_0= ruleMediaType ) ) otherlv_6= ']' otherlv_7= 'uri' otherlv_8= '=' ( (lv_uri_9_0= RULE_STRING ) ) otherlv_10= 'external' otherlv_11= '=' ( (lv_external_12_0= ruleEBoolean ) ) otherlv_13= '{' ( (lv_ports_14_0= rulePort ) )* otherlv_15= '}' ) ;
    public final EObject ruleResource() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        Token otherlv_8=null;
        Token lv_uri_9_0=null;
        Token otherlv_10=null;
        Token otherlv_11=null;
        Token otherlv_13=null;
        Token otherlv_15=null;
        Enumerator lv_mediaType_5_0 = null;

        AntlrDatatypeRuleToken lv_external_12_0 = null;

        EObject lv_ports_14_0 = null;



        	enterRule();

        try {
            // InternalMediaFlow.g:262:2: ( (otherlv_0= 'resource' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '[' otherlv_3= 'mediaType' otherlv_4= '=' ( (lv_mediaType_5_0= ruleMediaType ) ) otherlv_6= ']' otherlv_7= 'uri' otherlv_8= '=' ( (lv_uri_9_0= RULE_STRING ) ) otherlv_10= 'external' otherlv_11= '=' ( (lv_external_12_0= ruleEBoolean ) ) otherlv_13= '{' ( (lv_ports_14_0= rulePort ) )* otherlv_15= '}' ) )
            // InternalMediaFlow.g:263:2: (otherlv_0= 'resource' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '[' otherlv_3= 'mediaType' otherlv_4= '=' ( (lv_mediaType_5_0= ruleMediaType ) ) otherlv_6= ']' otherlv_7= 'uri' otherlv_8= '=' ( (lv_uri_9_0= RULE_STRING ) ) otherlv_10= 'external' otherlv_11= '=' ( (lv_external_12_0= ruleEBoolean ) ) otherlv_13= '{' ( (lv_ports_14_0= rulePort ) )* otherlv_15= '}' )
            {
            // InternalMediaFlow.g:263:2: (otherlv_0= 'resource' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '[' otherlv_3= 'mediaType' otherlv_4= '=' ( (lv_mediaType_5_0= ruleMediaType ) ) otherlv_6= ']' otherlv_7= 'uri' otherlv_8= '=' ( (lv_uri_9_0= RULE_STRING ) ) otherlv_10= 'external' otherlv_11= '=' ( (lv_external_12_0= ruleEBoolean ) ) otherlv_13= '{' ( (lv_ports_14_0= rulePort ) )* otherlv_15= '}' )
            // InternalMediaFlow.g:264:3: otherlv_0= 'resource' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '[' otherlv_3= 'mediaType' otherlv_4= '=' ( (lv_mediaType_5_0= ruleMediaType ) ) otherlv_6= ']' otherlv_7= 'uri' otherlv_8= '=' ( (lv_uri_9_0= RULE_STRING ) ) otherlv_10= 'external' otherlv_11= '=' ( (lv_external_12_0= ruleEBoolean ) ) otherlv_13= '{' ( (lv_ports_14_0= rulePort ) )* otherlv_15= '}'
            {
            otherlv_0=(Token)match(input,14,FOLLOW_3); 

            			newLeafNode(otherlv_0, grammarAccess.getResourceAccess().getResourceKeyword_0());
            		
            // InternalMediaFlow.g:268:3: ( (lv_name_1_0= RULE_ID ) )
            // InternalMediaFlow.g:269:4: (lv_name_1_0= RULE_ID )
            {
            // InternalMediaFlow.g:269:4: (lv_name_1_0= RULE_ID )
            // InternalMediaFlow.g:270:5: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_7); 

            					newLeafNode(lv_name_1_0, grammarAccess.getResourceAccess().getNameIDTerminalRuleCall_1_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getResourceRule());
            					}
            					setWithLastConsumed(
            						current,
            						"name",
            						lv_name_1_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }

            otherlv_2=(Token)match(input,15,FOLLOW_8); 

            			newLeafNode(otherlv_2, grammarAccess.getResourceAccess().getLeftSquareBracketKeyword_2());
            		
            otherlv_3=(Token)match(input,16,FOLLOW_9); 

            			newLeafNode(otherlv_3, grammarAccess.getResourceAccess().getMediaTypeKeyword_3());
            		
            otherlv_4=(Token)match(input,17,FOLLOW_10); 

            			newLeafNode(otherlv_4, grammarAccess.getResourceAccess().getEqualsSignKeyword_4());
            		
            // InternalMediaFlow.g:298:3: ( (lv_mediaType_5_0= ruleMediaType ) )
            // InternalMediaFlow.g:299:4: (lv_mediaType_5_0= ruleMediaType )
            {
            // InternalMediaFlow.g:299:4: (lv_mediaType_5_0= ruleMediaType )
            // InternalMediaFlow.g:300:5: lv_mediaType_5_0= ruleMediaType
            {

            					newCompositeNode(grammarAccess.getResourceAccess().getMediaTypeMediaTypeEnumRuleCall_5_0());
            				
            pushFollow(FOLLOW_11);
            lv_mediaType_5_0=ruleMediaType();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getResourceRule());
            					}
            					set(
            						current,
            						"mediaType",
            						lv_mediaType_5_0,
            						"yohannis.alfa.mediaflow.MediaFlow.MediaType");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            otherlv_6=(Token)match(input,18,FOLLOW_12); 

            			newLeafNode(otherlv_6, grammarAccess.getResourceAccess().getRightSquareBracketKeyword_6());
            		
            otherlv_7=(Token)match(input,19,FOLLOW_9); 

            			newLeafNode(otherlv_7, grammarAccess.getResourceAccess().getUriKeyword_7());
            		
            otherlv_8=(Token)match(input,17,FOLLOW_13); 

            			newLeafNode(otherlv_8, grammarAccess.getResourceAccess().getEqualsSignKeyword_8());
            		
            // InternalMediaFlow.g:329:3: ( (lv_uri_9_0= RULE_STRING ) )
            // InternalMediaFlow.g:330:4: (lv_uri_9_0= RULE_STRING )
            {
            // InternalMediaFlow.g:330:4: (lv_uri_9_0= RULE_STRING )
            // InternalMediaFlow.g:331:5: lv_uri_9_0= RULE_STRING
            {
            lv_uri_9_0=(Token)match(input,RULE_STRING,FOLLOW_14); 

            					newLeafNode(lv_uri_9_0, grammarAccess.getResourceAccess().getUriSTRINGTerminalRuleCall_9_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getResourceRule());
            					}
            					setWithLastConsumed(
            						current,
            						"uri",
            						lv_uri_9_0,
            						"org.eclipse.xtext.common.Terminals.STRING");
            				

            }


            }

            otherlv_10=(Token)match(input,20,FOLLOW_9); 

            			newLeafNode(otherlv_10, grammarAccess.getResourceAccess().getExternalKeyword_10());
            		
            otherlv_11=(Token)match(input,17,FOLLOW_15); 

            			newLeafNode(otherlv_11, grammarAccess.getResourceAccess().getEqualsSignKeyword_11());
            		
            // InternalMediaFlow.g:355:3: ( (lv_external_12_0= ruleEBoolean ) )
            // InternalMediaFlow.g:356:4: (lv_external_12_0= ruleEBoolean )
            {
            // InternalMediaFlow.g:356:4: (lv_external_12_0= ruleEBoolean )
            // InternalMediaFlow.g:357:5: lv_external_12_0= ruleEBoolean
            {

            					newCompositeNode(grammarAccess.getResourceAccess().getExternalEBooleanParserRuleCall_12_0());
            				
            pushFollow(FOLLOW_4);
            lv_external_12_0=ruleEBoolean();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getResourceRule());
            					}
            					set(
            						current,
            						"external",
            						lv_external_12_0,
            						"yohannis.alfa.mediaflow.MediaFlow.EBoolean");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            otherlv_13=(Token)match(input,12,FOLLOW_16); 

            			newLeafNode(otherlv_13, grammarAccess.getResourceAccess().getLeftCurlyBracketKeyword_13());
            		
            // InternalMediaFlow.g:378:3: ( (lv_ports_14_0= rulePort ) )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==32) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // InternalMediaFlow.g:379:4: (lv_ports_14_0= rulePort )
            	    {
            	    // InternalMediaFlow.g:379:4: (lv_ports_14_0= rulePort )
            	    // InternalMediaFlow.g:380:5: lv_ports_14_0= rulePort
            	    {

            	    					newCompositeNode(grammarAccess.getResourceAccess().getPortsPortParserRuleCall_14_0());
            	    				
            	    pushFollow(FOLLOW_16);
            	    lv_ports_14_0=rulePort();

            	    state._fsp--;


            	    					if (current==null) {
            	    						current = createModelElementForParent(grammarAccess.getResourceRule());
            	    					}
            	    					add(
            	    						current,
            	    						"ports",
            	    						lv_ports_14_0,
            	    						"yohannis.alfa.mediaflow.MediaFlow.Port");
            	    					afterParserOrEnumRuleCall();
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            otherlv_15=(Token)match(input,13,FOLLOW_2); 

            			newLeafNode(otherlv_15, grammarAccess.getResourceAccess().getRightCurlyBracketKeyword_15());
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleResource"


    // $ANTLR start "entryRuleScaler"
    // InternalMediaFlow.g:405:1: entryRuleScaler returns [EObject current=null] : iv_ruleScaler= ruleScaler EOF ;
    public final EObject entryRuleScaler() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleScaler = null;


        try {
            // InternalMediaFlow.g:405:47: (iv_ruleScaler= ruleScaler EOF )
            // InternalMediaFlow.g:406:2: iv_ruleScaler= ruleScaler EOF
            {
             newCompositeNode(grammarAccess.getScalerRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleScaler=ruleScaler();

            state._fsp--;

             current =iv_ruleScaler; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleScaler"


    // $ANTLR start "ruleScaler"
    // InternalMediaFlow.g:412:1: ruleScaler returns [EObject current=null] : (otherlv_0= 'scaler' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= 'backend' otherlv_3= '=' ( (lv_backend_4_0= ruleBackend ) ) otherlv_5= 'command' otherlv_6= '=' ( (lv_command_7_0= RULE_STRING ) ) otherlv_8= 'replicas' otherlv_9= '=' ( (lv_replicas_10_0= RULE_INT ) ) otherlv_11= 'width' otherlv_12= '=' ( (lv_width_13_0= RULE_INT ) ) otherlv_14= 'height' otherlv_15= '=' ( (lv_height_16_0= RULE_INT ) ) otherlv_17= '{' ( (lv_ports_18_0= rulePort ) )* otherlv_19= '}' ) ;
    public final EObject ruleScaler() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token lv_command_7_0=null;
        Token otherlv_8=null;
        Token otherlv_9=null;
        Token lv_replicas_10_0=null;
        Token otherlv_11=null;
        Token otherlv_12=null;
        Token lv_width_13_0=null;
        Token otherlv_14=null;
        Token otherlv_15=null;
        Token lv_height_16_0=null;
        Token otherlv_17=null;
        Token otherlv_19=null;
        Enumerator lv_backend_4_0 = null;

        EObject lv_ports_18_0 = null;



        	enterRule();

        try {
            // InternalMediaFlow.g:418:2: ( (otherlv_0= 'scaler' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= 'backend' otherlv_3= '=' ( (lv_backend_4_0= ruleBackend ) ) otherlv_5= 'command' otherlv_6= '=' ( (lv_command_7_0= RULE_STRING ) ) otherlv_8= 'replicas' otherlv_9= '=' ( (lv_replicas_10_0= RULE_INT ) ) otherlv_11= 'width' otherlv_12= '=' ( (lv_width_13_0= RULE_INT ) ) otherlv_14= 'height' otherlv_15= '=' ( (lv_height_16_0= RULE_INT ) ) otherlv_17= '{' ( (lv_ports_18_0= rulePort ) )* otherlv_19= '}' ) )
            // InternalMediaFlow.g:419:2: (otherlv_0= 'scaler' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= 'backend' otherlv_3= '=' ( (lv_backend_4_0= ruleBackend ) ) otherlv_5= 'command' otherlv_6= '=' ( (lv_command_7_0= RULE_STRING ) ) otherlv_8= 'replicas' otherlv_9= '=' ( (lv_replicas_10_0= RULE_INT ) ) otherlv_11= 'width' otherlv_12= '=' ( (lv_width_13_0= RULE_INT ) ) otherlv_14= 'height' otherlv_15= '=' ( (lv_height_16_0= RULE_INT ) ) otherlv_17= '{' ( (lv_ports_18_0= rulePort ) )* otherlv_19= '}' )
            {
            // InternalMediaFlow.g:419:2: (otherlv_0= 'scaler' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= 'backend' otherlv_3= '=' ( (lv_backend_4_0= ruleBackend ) ) otherlv_5= 'command' otherlv_6= '=' ( (lv_command_7_0= RULE_STRING ) ) otherlv_8= 'replicas' otherlv_9= '=' ( (lv_replicas_10_0= RULE_INT ) ) otherlv_11= 'width' otherlv_12= '=' ( (lv_width_13_0= RULE_INT ) ) otherlv_14= 'height' otherlv_15= '=' ( (lv_height_16_0= RULE_INT ) ) otherlv_17= '{' ( (lv_ports_18_0= rulePort ) )* otherlv_19= '}' )
            // InternalMediaFlow.g:420:3: otherlv_0= 'scaler' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= 'backend' otherlv_3= '=' ( (lv_backend_4_0= ruleBackend ) ) otherlv_5= 'command' otherlv_6= '=' ( (lv_command_7_0= RULE_STRING ) ) otherlv_8= 'replicas' otherlv_9= '=' ( (lv_replicas_10_0= RULE_INT ) ) otherlv_11= 'width' otherlv_12= '=' ( (lv_width_13_0= RULE_INT ) ) otherlv_14= 'height' otherlv_15= '=' ( (lv_height_16_0= RULE_INT ) ) otherlv_17= '{' ( (lv_ports_18_0= rulePort ) )* otherlv_19= '}'
            {
            otherlv_0=(Token)match(input,21,FOLLOW_3); 

            			newLeafNode(otherlv_0, grammarAccess.getScalerAccess().getScalerKeyword_0());
            		
            // InternalMediaFlow.g:424:3: ( (lv_name_1_0= RULE_ID ) )
            // InternalMediaFlow.g:425:4: (lv_name_1_0= RULE_ID )
            {
            // InternalMediaFlow.g:425:4: (lv_name_1_0= RULE_ID )
            // InternalMediaFlow.g:426:5: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_17); 

            					newLeafNode(lv_name_1_0, grammarAccess.getScalerAccess().getNameIDTerminalRuleCall_1_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getScalerRule());
            					}
            					setWithLastConsumed(
            						current,
            						"name",
            						lv_name_1_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }

            otherlv_2=(Token)match(input,22,FOLLOW_9); 

            			newLeafNode(otherlv_2, grammarAccess.getScalerAccess().getBackendKeyword_2());
            		
            otherlv_3=(Token)match(input,17,FOLLOW_18); 

            			newLeafNode(otherlv_3, grammarAccess.getScalerAccess().getEqualsSignKeyword_3());
            		
            // InternalMediaFlow.g:450:3: ( (lv_backend_4_0= ruleBackend ) )
            // InternalMediaFlow.g:451:4: (lv_backend_4_0= ruleBackend )
            {
            // InternalMediaFlow.g:451:4: (lv_backend_4_0= ruleBackend )
            // InternalMediaFlow.g:452:5: lv_backend_4_0= ruleBackend
            {

            					newCompositeNode(grammarAccess.getScalerAccess().getBackendBackendEnumRuleCall_4_0());
            				
            pushFollow(FOLLOW_19);
            lv_backend_4_0=ruleBackend();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getScalerRule());
            					}
            					set(
            						current,
            						"backend",
            						lv_backend_4_0,
            						"yohannis.alfa.mediaflow.MediaFlow.Backend");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            otherlv_5=(Token)match(input,23,FOLLOW_9); 

            			newLeafNode(otherlv_5, grammarAccess.getScalerAccess().getCommandKeyword_5());
            		
            otherlv_6=(Token)match(input,17,FOLLOW_13); 

            			newLeafNode(otherlv_6, grammarAccess.getScalerAccess().getEqualsSignKeyword_6());
            		
            // InternalMediaFlow.g:477:3: ( (lv_command_7_0= RULE_STRING ) )
            // InternalMediaFlow.g:478:4: (lv_command_7_0= RULE_STRING )
            {
            // InternalMediaFlow.g:478:4: (lv_command_7_0= RULE_STRING )
            // InternalMediaFlow.g:479:5: lv_command_7_0= RULE_STRING
            {
            lv_command_7_0=(Token)match(input,RULE_STRING,FOLLOW_20); 

            					newLeafNode(lv_command_7_0, grammarAccess.getScalerAccess().getCommandSTRINGTerminalRuleCall_7_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getScalerRule());
            					}
            					setWithLastConsumed(
            						current,
            						"command",
            						lv_command_7_0,
            						"org.eclipse.xtext.common.Terminals.STRING");
            				

            }


            }

            otherlv_8=(Token)match(input,24,FOLLOW_9); 

            			newLeafNode(otherlv_8, grammarAccess.getScalerAccess().getReplicasKeyword_8());
            		
            otherlv_9=(Token)match(input,17,FOLLOW_21); 

            			newLeafNode(otherlv_9, grammarAccess.getScalerAccess().getEqualsSignKeyword_9());
            		
            // InternalMediaFlow.g:503:3: ( (lv_replicas_10_0= RULE_INT ) )
            // InternalMediaFlow.g:504:4: (lv_replicas_10_0= RULE_INT )
            {
            // InternalMediaFlow.g:504:4: (lv_replicas_10_0= RULE_INT )
            // InternalMediaFlow.g:505:5: lv_replicas_10_0= RULE_INT
            {
            lv_replicas_10_0=(Token)match(input,RULE_INT,FOLLOW_22); 

            					newLeafNode(lv_replicas_10_0, grammarAccess.getScalerAccess().getReplicasINTTerminalRuleCall_10_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getScalerRule());
            					}
            					setWithLastConsumed(
            						current,
            						"replicas",
            						lv_replicas_10_0,
            						"org.eclipse.xtext.common.Terminals.INT");
            				

            }


            }

            otherlv_11=(Token)match(input,25,FOLLOW_9); 

            			newLeafNode(otherlv_11, grammarAccess.getScalerAccess().getWidthKeyword_11());
            		
            otherlv_12=(Token)match(input,17,FOLLOW_21); 

            			newLeafNode(otherlv_12, grammarAccess.getScalerAccess().getEqualsSignKeyword_12());
            		
            // InternalMediaFlow.g:529:3: ( (lv_width_13_0= RULE_INT ) )
            // InternalMediaFlow.g:530:4: (lv_width_13_0= RULE_INT )
            {
            // InternalMediaFlow.g:530:4: (lv_width_13_0= RULE_INT )
            // InternalMediaFlow.g:531:5: lv_width_13_0= RULE_INT
            {
            lv_width_13_0=(Token)match(input,RULE_INT,FOLLOW_23); 

            					newLeafNode(lv_width_13_0, grammarAccess.getScalerAccess().getWidthINTTerminalRuleCall_13_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getScalerRule());
            					}
            					setWithLastConsumed(
            						current,
            						"width",
            						lv_width_13_0,
            						"org.eclipse.xtext.common.Terminals.INT");
            				

            }


            }

            otherlv_14=(Token)match(input,26,FOLLOW_9); 

            			newLeafNode(otherlv_14, grammarAccess.getScalerAccess().getHeightKeyword_14());
            		
            otherlv_15=(Token)match(input,17,FOLLOW_21); 

            			newLeafNode(otherlv_15, grammarAccess.getScalerAccess().getEqualsSignKeyword_15());
            		
            // InternalMediaFlow.g:555:3: ( (lv_height_16_0= RULE_INT ) )
            // InternalMediaFlow.g:556:4: (lv_height_16_0= RULE_INT )
            {
            // InternalMediaFlow.g:556:4: (lv_height_16_0= RULE_INT )
            // InternalMediaFlow.g:557:5: lv_height_16_0= RULE_INT
            {
            lv_height_16_0=(Token)match(input,RULE_INT,FOLLOW_4); 

            					newLeafNode(lv_height_16_0, grammarAccess.getScalerAccess().getHeightINTTerminalRuleCall_16_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getScalerRule());
            					}
            					setWithLastConsumed(
            						current,
            						"height",
            						lv_height_16_0,
            						"org.eclipse.xtext.common.Terminals.INT");
            				

            }


            }

            otherlv_17=(Token)match(input,12,FOLLOW_16); 

            			newLeafNode(otherlv_17, grammarAccess.getScalerAccess().getLeftCurlyBracketKeyword_17());
            		
            // InternalMediaFlow.g:577:3: ( (lv_ports_18_0= rulePort ) )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==32) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // InternalMediaFlow.g:578:4: (lv_ports_18_0= rulePort )
            	    {
            	    // InternalMediaFlow.g:578:4: (lv_ports_18_0= rulePort )
            	    // InternalMediaFlow.g:579:5: lv_ports_18_0= rulePort
            	    {

            	    					newCompositeNode(grammarAccess.getScalerAccess().getPortsPortParserRuleCall_18_0());
            	    				
            	    pushFollow(FOLLOW_16);
            	    lv_ports_18_0=rulePort();

            	    state._fsp--;


            	    					if (current==null) {
            	    						current = createModelElementForParent(grammarAccess.getScalerRule());
            	    					}
            	    					add(
            	    						current,
            	    						"ports",
            	    						lv_ports_18_0,
            	    						"yohannis.alfa.mediaflow.MediaFlow.Port");
            	    					afterParserOrEnumRuleCall();
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            otherlv_19=(Token)match(input,13,FOLLOW_2); 

            			newLeafNode(otherlv_19, grammarAccess.getScalerAccess().getRightCurlyBracketKeyword_19());
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleScaler"


    // $ANTLR start "entryRuleTranscoder"
    // InternalMediaFlow.g:604:1: entryRuleTranscoder returns [EObject current=null] : iv_ruleTranscoder= ruleTranscoder EOF ;
    public final EObject entryRuleTranscoder() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTranscoder = null;


        try {
            // InternalMediaFlow.g:604:51: (iv_ruleTranscoder= ruleTranscoder EOF )
            // InternalMediaFlow.g:605:2: iv_ruleTranscoder= ruleTranscoder EOF
            {
             newCompositeNode(grammarAccess.getTranscoderRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTranscoder=ruleTranscoder();

            state._fsp--;

             current =iv_ruleTranscoder; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTranscoder"


    // $ANTLR start "ruleTranscoder"
    // InternalMediaFlow.g:611:1: ruleTranscoder returns [EObject current=null] : (otherlv_0= 'transcoder' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= 'backend' otherlv_3= '=' ( (lv_backend_4_0= ruleBackend ) ) otherlv_5= 'command' otherlv_6= '=' ( (lv_command_7_0= RULE_STRING ) ) otherlv_8= 'replicas' otherlv_9= '=' ( (lv_replicas_10_0= RULE_INT ) ) otherlv_11= 'videoCodec' otherlv_12= '=' ( (lv_videoCodec_13_0= RULE_STRING ) ) otherlv_14= 'audioCodec' otherlv_15= '=' ( (lv_audioCodec_16_0= RULE_STRING ) ) otherlv_17= 'container' otherlv_18= '=' ( (lv_container_19_0= RULE_STRING ) ) otherlv_20= 'bitrate' otherlv_21= '=' ( (lv_bitrate_22_0= RULE_INT ) ) otherlv_23= '{' ( (lv_ports_24_0= rulePort ) )* otherlv_25= '}' ) ;
    public final EObject ruleTranscoder() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token lv_command_7_0=null;
        Token otherlv_8=null;
        Token otherlv_9=null;
        Token lv_replicas_10_0=null;
        Token otherlv_11=null;
        Token otherlv_12=null;
        Token lv_videoCodec_13_0=null;
        Token otherlv_14=null;
        Token otherlv_15=null;
        Token lv_audioCodec_16_0=null;
        Token otherlv_17=null;
        Token otherlv_18=null;
        Token lv_container_19_0=null;
        Token otherlv_20=null;
        Token otherlv_21=null;
        Token lv_bitrate_22_0=null;
        Token otherlv_23=null;
        Token otherlv_25=null;
        Enumerator lv_backend_4_0 = null;

        EObject lv_ports_24_0 = null;



        	enterRule();

        try {
            // InternalMediaFlow.g:617:2: ( (otherlv_0= 'transcoder' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= 'backend' otherlv_3= '=' ( (lv_backend_4_0= ruleBackend ) ) otherlv_5= 'command' otherlv_6= '=' ( (lv_command_7_0= RULE_STRING ) ) otherlv_8= 'replicas' otherlv_9= '=' ( (lv_replicas_10_0= RULE_INT ) ) otherlv_11= 'videoCodec' otherlv_12= '=' ( (lv_videoCodec_13_0= RULE_STRING ) ) otherlv_14= 'audioCodec' otherlv_15= '=' ( (lv_audioCodec_16_0= RULE_STRING ) ) otherlv_17= 'container' otherlv_18= '=' ( (lv_container_19_0= RULE_STRING ) ) otherlv_20= 'bitrate' otherlv_21= '=' ( (lv_bitrate_22_0= RULE_INT ) ) otherlv_23= '{' ( (lv_ports_24_0= rulePort ) )* otherlv_25= '}' ) )
            // InternalMediaFlow.g:618:2: (otherlv_0= 'transcoder' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= 'backend' otherlv_3= '=' ( (lv_backend_4_0= ruleBackend ) ) otherlv_5= 'command' otherlv_6= '=' ( (lv_command_7_0= RULE_STRING ) ) otherlv_8= 'replicas' otherlv_9= '=' ( (lv_replicas_10_0= RULE_INT ) ) otherlv_11= 'videoCodec' otherlv_12= '=' ( (lv_videoCodec_13_0= RULE_STRING ) ) otherlv_14= 'audioCodec' otherlv_15= '=' ( (lv_audioCodec_16_0= RULE_STRING ) ) otherlv_17= 'container' otherlv_18= '=' ( (lv_container_19_0= RULE_STRING ) ) otherlv_20= 'bitrate' otherlv_21= '=' ( (lv_bitrate_22_0= RULE_INT ) ) otherlv_23= '{' ( (lv_ports_24_0= rulePort ) )* otherlv_25= '}' )
            {
            // InternalMediaFlow.g:618:2: (otherlv_0= 'transcoder' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= 'backend' otherlv_3= '=' ( (lv_backend_4_0= ruleBackend ) ) otherlv_5= 'command' otherlv_6= '=' ( (lv_command_7_0= RULE_STRING ) ) otherlv_8= 'replicas' otherlv_9= '=' ( (lv_replicas_10_0= RULE_INT ) ) otherlv_11= 'videoCodec' otherlv_12= '=' ( (lv_videoCodec_13_0= RULE_STRING ) ) otherlv_14= 'audioCodec' otherlv_15= '=' ( (lv_audioCodec_16_0= RULE_STRING ) ) otherlv_17= 'container' otherlv_18= '=' ( (lv_container_19_0= RULE_STRING ) ) otherlv_20= 'bitrate' otherlv_21= '=' ( (lv_bitrate_22_0= RULE_INT ) ) otherlv_23= '{' ( (lv_ports_24_0= rulePort ) )* otherlv_25= '}' )
            // InternalMediaFlow.g:619:3: otherlv_0= 'transcoder' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= 'backend' otherlv_3= '=' ( (lv_backend_4_0= ruleBackend ) ) otherlv_5= 'command' otherlv_6= '=' ( (lv_command_7_0= RULE_STRING ) ) otherlv_8= 'replicas' otherlv_9= '=' ( (lv_replicas_10_0= RULE_INT ) ) otherlv_11= 'videoCodec' otherlv_12= '=' ( (lv_videoCodec_13_0= RULE_STRING ) ) otherlv_14= 'audioCodec' otherlv_15= '=' ( (lv_audioCodec_16_0= RULE_STRING ) ) otherlv_17= 'container' otherlv_18= '=' ( (lv_container_19_0= RULE_STRING ) ) otherlv_20= 'bitrate' otherlv_21= '=' ( (lv_bitrate_22_0= RULE_INT ) ) otherlv_23= '{' ( (lv_ports_24_0= rulePort ) )* otherlv_25= '}'
            {
            otherlv_0=(Token)match(input,27,FOLLOW_3); 

            			newLeafNode(otherlv_0, grammarAccess.getTranscoderAccess().getTranscoderKeyword_0());
            		
            // InternalMediaFlow.g:623:3: ( (lv_name_1_0= RULE_ID ) )
            // InternalMediaFlow.g:624:4: (lv_name_1_0= RULE_ID )
            {
            // InternalMediaFlow.g:624:4: (lv_name_1_0= RULE_ID )
            // InternalMediaFlow.g:625:5: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_17); 

            					newLeafNode(lv_name_1_0, grammarAccess.getTranscoderAccess().getNameIDTerminalRuleCall_1_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getTranscoderRule());
            					}
            					setWithLastConsumed(
            						current,
            						"name",
            						lv_name_1_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }

            otherlv_2=(Token)match(input,22,FOLLOW_9); 

            			newLeafNode(otherlv_2, grammarAccess.getTranscoderAccess().getBackendKeyword_2());
            		
            otherlv_3=(Token)match(input,17,FOLLOW_18); 

            			newLeafNode(otherlv_3, grammarAccess.getTranscoderAccess().getEqualsSignKeyword_3());
            		
            // InternalMediaFlow.g:649:3: ( (lv_backend_4_0= ruleBackend ) )
            // InternalMediaFlow.g:650:4: (lv_backend_4_0= ruleBackend )
            {
            // InternalMediaFlow.g:650:4: (lv_backend_4_0= ruleBackend )
            // InternalMediaFlow.g:651:5: lv_backend_4_0= ruleBackend
            {

            					newCompositeNode(grammarAccess.getTranscoderAccess().getBackendBackendEnumRuleCall_4_0());
            				
            pushFollow(FOLLOW_19);
            lv_backend_4_0=ruleBackend();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getTranscoderRule());
            					}
            					set(
            						current,
            						"backend",
            						lv_backend_4_0,
            						"yohannis.alfa.mediaflow.MediaFlow.Backend");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            otherlv_5=(Token)match(input,23,FOLLOW_9); 

            			newLeafNode(otherlv_5, grammarAccess.getTranscoderAccess().getCommandKeyword_5());
            		
            otherlv_6=(Token)match(input,17,FOLLOW_13); 

            			newLeafNode(otherlv_6, grammarAccess.getTranscoderAccess().getEqualsSignKeyword_6());
            		
            // InternalMediaFlow.g:676:3: ( (lv_command_7_0= RULE_STRING ) )
            // InternalMediaFlow.g:677:4: (lv_command_7_0= RULE_STRING )
            {
            // InternalMediaFlow.g:677:4: (lv_command_7_0= RULE_STRING )
            // InternalMediaFlow.g:678:5: lv_command_7_0= RULE_STRING
            {
            lv_command_7_0=(Token)match(input,RULE_STRING,FOLLOW_20); 

            					newLeafNode(lv_command_7_0, grammarAccess.getTranscoderAccess().getCommandSTRINGTerminalRuleCall_7_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getTranscoderRule());
            					}
            					setWithLastConsumed(
            						current,
            						"command",
            						lv_command_7_0,
            						"org.eclipse.xtext.common.Terminals.STRING");
            				

            }


            }

            otherlv_8=(Token)match(input,24,FOLLOW_9); 

            			newLeafNode(otherlv_8, grammarAccess.getTranscoderAccess().getReplicasKeyword_8());
            		
            otherlv_9=(Token)match(input,17,FOLLOW_21); 

            			newLeafNode(otherlv_9, grammarAccess.getTranscoderAccess().getEqualsSignKeyword_9());
            		
            // InternalMediaFlow.g:702:3: ( (lv_replicas_10_0= RULE_INT ) )
            // InternalMediaFlow.g:703:4: (lv_replicas_10_0= RULE_INT )
            {
            // InternalMediaFlow.g:703:4: (lv_replicas_10_0= RULE_INT )
            // InternalMediaFlow.g:704:5: lv_replicas_10_0= RULE_INT
            {
            lv_replicas_10_0=(Token)match(input,RULE_INT,FOLLOW_24); 

            					newLeafNode(lv_replicas_10_0, grammarAccess.getTranscoderAccess().getReplicasINTTerminalRuleCall_10_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getTranscoderRule());
            					}
            					setWithLastConsumed(
            						current,
            						"replicas",
            						lv_replicas_10_0,
            						"org.eclipse.xtext.common.Terminals.INT");
            				

            }


            }

            otherlv_11=(Token)match(input,28,FOLLOW_9); 

            			newLeafNode(otherlv_11, grammarAccess.getTranscoderAccess().getVideoCodecKeyword_11());
            		
            otherlv_12=(Token)match(input,17,FOLLOW_13); 

            			newLeafNode(otherlv_12, grammarAccess.getTranscoderAccess().getEqualsSignKeyword_12());
            		
            // InternalMediaFlow.g:728:3: ( (lv_videoCodec_13_0= RULE_STRING ) )
            // InternalMediaFlow.g:729:4: (lv_videoCodec_13_0= RULE_STRING )
            {
            // InternalMediaFlow.g:729:4: (lv_videoCodec_13_0= RULE_STRING )
            // InternalMediaFlow.g:730:5: lv_videoCodec_13_0= RULE_STRING
            {
            lv_videoCodec_13_0=(Token)match(input,RULE_STRING,FOLLOW_25); 

            					newLeafNode(lv_videoCodec_13_0, grammarAccess.getTranscoderAccess().getVideoCodecSTRINGTerminalRuleCall_13_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getTranscoderRule());
            					}
            					setWithLastConsumed(
            						current,
            						"videoCodec",
            						lv_videoCodec_13_0,
            						"org.eclipse.xtext.common.Terminals.STRING");
            				

            }


            }

            otherlv_14=(Token)match(input,29,FOLLOW_9); 

            			newLeafNode(otherlv_14, grammarAccess.getTranscoderAccess().getAudioCodecKeyword_14());
            		
            otherlv_15=(Token)match(input,17,FOLLOW_13); 

            			newLeafNode(otherlv_15, grammarAccess.getTranscoderAccess().getEqualsSignKeyword_15());
            		
            // InternalMediaFlow.g:754:3: ( (lv_audioCodec_16_0= RULE_STRING ) )
            // InternalMediaFlow.g:755:4: (lv_audioCodec_16_0= RULE_STRING )
            {
            // InternalMediaFlow.g:755:4: (lv_audioCodec_16_0= RULE_STRING )
            // InternalMediaFlow.g:756:5: lv_audioCodec_16_0= RULE_STRING
            {
            lv_audioCodec_16_0=(Token)match(input,RULE_STRING,FOLLOW_26); 

            					newLeafNode(lv_audioCodec_16_0, grammarAccess.getTranscoderAccess().getAudioCodecSTRINGTerminalRuleCall_16_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getTranscoderRule());
            					}
            					setWithLastConsumed(
            						current,
            						"audioCodec",
            						lv_audioCodec_16_0,
            						"org.eclipse.xtext.common.Terminals.STRING");
            				

            }


            }

            otherlv_17=(Token)match(input,30,FOLLOW_9); 

            			newLeafNode(otherlv_17, grammarAccess.getTranscoderAccess().getContainerKeyword_17());
            		
            otherlv_18=(Token)match(input,17,FOLLOW_13); 

            			newLeafNode(otherlv_18, grammarAccess.getTranscoderAccess().getEqualsSignKeyword_18());
            		
            // InternalMediaFlow.g:780:3: ( (lv_container_19_0= RULE_STRING ) )
            // InternalMediaFlow.g:781:4: (lv_container_19_0= RULE_STRING )
            {
            // InternalMediaFlow.g:781:4: (lv_container_19_0= RULE_STRING )
            // InternalMediaFlow.g:782:5: lv_container_19_0= RULE_STRING
            {
            lv_container_19_0=(Token)match(input,RULE_STRING,FOLLOW_27); 

            					newLeafNode(lv_container_19_0, grammarAccess.getTranscoderAccess().getContainerSTRINGTerminalRuleCall_19_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getTranscoderRule());
            					}
            					setWithLastConsumed(
            						current,
            						"container",
            						lv_container_19_0,
            						"org.eclipse.xtext.common.Terminals.STRING");
            				

            }


            }

            otherlv_20=(Token)match(input,31,FOLLOW_9); 

            			newLeafNode(otherlv_20, grammarAccess.getTranscoderAccess().getBitrateKeyword_20());
            		
            otherlv_21=(Token)match(input,17,FOLLOW_21); 

            			newLeafNode(otherlv_21, grammarAccess.getTranscoderAccess().getEqualsSignKeyword_21());
            		
            // InternalMediaFlow.g:806:3: ( (lv_bitrate_22_0= RULE_INT ) )
            // InternalMediaFlow.g:807:4: (lv_bitrate_22_0= RULE_INT )
            {
            // InternalMediaFlow.g:807:4: (lv_bitrate_22_0= RULE_INT )
            // InternalMediaFlow.g:808:5: lv_bitrate_22_0= RULE_INT
            {
            lv_bitrate_22_0=(Token)match(input,RULE_INT,FOLLOW_4); 

            					newLeafNode(lv_bitrate_22_0, grammarAccess.getTranscoderAccess().getBitrateINTTerminalRuleCall_22_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getTranscoderRule());
            					}
            					setWithLastConsumed(
            						current,
            						"bitrate",
            						lv_bitrate_22_0,
            						"org.eclipse.xtext.common.Terminals.INT");
            				

            }


            }

            otherlv_23=(Token)match(input,12,FOLLOW_16); 

            			newLeafNode(otherlv_23, grammarAccess.getTranscoderAccess().getLeftCurlyBracketKeyword_23());
            		
            // InternalMediaFlow.g:828:3: ( (lv_ports_24_0= rulePort ) )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==32) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // InternalMediaFlow.g:829:4: (lv_ports_24_0= rulePort )
            	    {
            	    // InternalMediaFlow.g:829:4: (lv_ports_24_0= rulePort )
            	    // InternalMediaFlow.g:830:5: lv_ports_24_0= rulePort
            	    {

            	    					newCompositeNode(grammarAccess.getTranscoderAccess().getPortsPortParserRuleCall_24_0());
            	    				
            	    pushFollow(FOLLOW_16);
            	    lv_ports_24_0=rulePort();

            	    state._fsp--;


            	    					if (current==null) {
            	    						current = createModelElementForParent(grammarAccess.getTranscoderRule());
            	    					}
            	    					add(
            	    						current,
            	    						"ports",
            	    						lv_ports_24_0,
            	    						"yohannis.alfa.mediaflow.MediaFlow.Port");
            	    					afterParserOrEnumRuleCall();
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);

            otherlv_25=(Token)match(input,13,FOLLOW_2); 

            			newLeafNode(otherlv_25, grammarAccess.getTranscoderAccess().getRightCurlyBracketKeyword_25());
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTranscoder"


    // $ANTLR start "entryRulePort"
    // InternalMediaFlow.g:855:1: entryRulePort returns [EObject current=null] : iv_rulePort= rulePort EOF ;
    public final EObject entryRulePort() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePort = null;


        try {
            // InternalMediaFlow.g:855:45: (iv_rulePort= rulePort EOF )
            // InternalMediaFlow.g:856:2: iv_rulePort= rulePort EOF
            {
             newCompositeNode(grammarAccess.getPortRule()); 
            pushFollow(FOLLOW_1);
            iv_rulePort=rulePort();

            state._fsp--;

             current =iv_rulePort; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePort"


    // $ANTLR start "rulePort"
    // InternalMediaFlow.g:862:1: rulePort returns [EObject current=null] : (otherlv_0= 'port' ( (lv_name_1_0= RULE_ID ) ) ( (lv_direction_2_0= ruleDirection ) ) ) ;
    public final EObject rulePort() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Enumerator lv_direction_2_0 = null;



        	enterRule();

        try {
            // InternalMediaFlow.g:868:2: ( (otherlv_0= 'port' ( (lv_name_1_0= RULE_ID ) ) ( (lv_direction_2_0= ruleDirection ) ) ) )
            // InternalMediaFlow.g:869:2: (otherlv_0= 'port' ( (lv_name_1_0= RULE_ID ) ) ( (lv_direction_2_0= ruleDirection ) ) )
            {
            // InternalMediaFlow.g:869:2: (otherlv_0= 'port' ( (lv_name_1_0= RULE_ID ) ) ( (lv_direction_2_0= ruleDirection ) ) )
            // InternalMediaFlow.g:870:3: otherlv_0= 'port' ( (lv_name_1_0= RULE_ID ) ) ( (lv_direction_2_0= ruleDirection ) )
            {
            otherlv_0=(Token)match(input,32,FOLLOW_3); 

            			newLeafNode(otherlv_0, grammarAccess.getPortAccess().getPortKeyword_0());
            		
            // InternalMediaFlow.g:874:3: ( (lv_name_1_0= RULE_ID ) )
            // InternalMediaFlow.g:875:4: (lv_name_1_0= RULE_ID )
            {
            // InternalMediaFlow.g:875:4: (lv_name_1_0= RULE_ID )
            // InternalMediaFlow.g:876:5: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_28); 

            					newLeafNode(lv_name_1_0, grammarAccess.getPortAccess().getNameIDTerminalRuleCall_1_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getPortRule());
            					}
            					setWithLastConsumed(
            						current,
            						"name",
            						lv_name_1_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }

            // InternalMediaFlow.g:892:3: ( (lv_direction_2_0= ruleDirection ) )
            // InternalMediaFlow.g:893:4: (lv_direction_2_0= ruleDirection )
            {
            // InternalMediaFlow.g:893:4: (lv_direction_2_0= ruleDirection )
            // InternalMediaFlow.g:894:5: lv_direction_2_0= ruleDirection
            {

            					newCompositeNode(grammarAccess.getPortAccess().getDirectionDirectionEnumRuleCall_2_0());
            				
            pushFollow(FOLLOW_2);
            lv_direction_2_0=ruleDirection();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getPortRule());
            					}
            					set(
            						current,
            						"direction",
            						lv_direction_2_0,
            						"yohannis.alfa.mediaflow.MediaFlow.Direction");
            					afterParserOrEnumRuleCall();
            				

            }


            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePort"


    // $ANTLR start "entryRuleEdge"
    // InternalMediaFlow.g:915:1: entryRuleEdge returns [EObject current=null] : iv_ruleEdge= ruleEdge EOF ;
    public final EObject entryRuleEdge() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEdge = null;


        try {
            // InternalMediaFlow.g:915:45: (iv_ruleEdge= ruleEdge EOF )
            // InternalMediaFlow.g:916:2: iv_ruleEdge= ruleEdge EOF
            {
             newCompositeNode(grammarAccess.getEdgeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleEdge=ruleEdge();

            state._fsp--;

             current =iv_ruleEdge; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleEdge"


    // $ANTLR start "ruleEdge"
    // InternalMediaFlow.g:922:1: ruleEdge returns [EObject current=null] : (otherlv_0= 'edge' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= 'from' ( (otherlv_3= RULE_ID ) ) otherlv_4= 'to' ( (otherlv_5= RULE_ID ) ) ) ;
    public final EObject ruleEdge() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_5=null;


        	enterRule();

        try {
            // InternalMediaFlow.g:928:2: ( (otherlv_0= 'edge' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= 'from' ( (otherlv_3= RULE_ID ) ) otherlv_4= 'to' ( (otherlv_5= RULE_ID ) ) ) )
            // InternalMediaFlow.g:929:2: (otherlv_0= 'edge' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= 'from' ( (otherlv_3= RULE_ID ) ) otherlv_4= 'to' ( (otherlv_5= RULE_ID ) ) )
            {
            // InternalMediaFlow.g:929:2: (otherlv_0= 'edge' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= 'from' ( (otherlv_3= RULE_ID ) ) otherlv_4= 'to' ( (otherlv_5= RULE_ID ) ) )
            // InternalMediaFlow.g:930:3: otherlv_0= 'edge' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= 'from' ( (otherlv_3= RULE_ID ) ) otherlv_4= 'to' ( (otherlv_5= RULE_ID ) )
            {
            otherlv_0=(Token)match(input,33,FOLLOW_3); 

            			newLeafNode(otherlv_0, grammarAccess.getEdgeAccess().getEdgeKeyword_0());
            		
            // InternalMediaFlow.g:934:3: ( (lv_name_1_0= RULE_ID ) )
            // InternalMediaFlow.g:935:4: (lv_name_1_0= RULE_ID )
            {
            // InternalMediaFlow.g:935:4: (lv_name_1_0= RULE_ID )
            // InternalMediaFlow.g:936:5: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_29); 

            					newLeafNode(lv_name_1_0, grammarAccess.getEdgeAccess().getNameIDTerminalRuleCall_1_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getEdgeRule());
            					}
            					setWithLastConsumed(
            						current,
            						"name",
            						lv_name_1_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }

            otherlv_2=(Token)match(input,34,FOLLOW_3); 

            			newLeafNode(otherlv_2, grammarAccess.getEdgeAccess().getFromKeyword_2());
            		
            // InternalMediaFlow.g:956:3: ( (otherlv_3= RULE_ID ) )
            // InternalMediaFlow.g:957:4: (otherlv_3= RULE_ID )
            {
            // InternalMediaFlow.g:957:4: (otherlv_3= RULE_ID )
            // InternalMediaFlow.g:958:5: otherlv_3= RULE_ID
            {

            					if (current==null) {
            						current = createModelElement(grammarAccess.getEdgeRule());
            					}
            				
            otherlv_3=(Token)match(input,RULE_ID,FOLLOW_30); 

            					newLeafNode(otherlv_3, grammarAccess.getEdgeAccess().getSourcePortCrossReference_3_0());
            				

            }


            }

            otherlv_4=(Token)match(input,35,FOLLOW_3); 

            			newLeafNode(otherlv_4, grammarAccess.getEdgeAccess().getToKeyword_4());
            		
            // InternalMediaFlow.g:973:3: ( (otherlv_5= RULE_ID ) )
            // InternalMediaFlow.g:974:4: (otherlv_5= RULE_ID )
            {
            // InternalMediaFlow.g:974:4: (otherlv_5= RULE_ID )
            // InternalMediaFlow.g:975:5: otherlv_5= RULE_ID
            {

            					if (current==null) {
            						current = createModelElement(grammarAccess.getEdgeRule());
            					}
            				
            otherlv_5=(Token)match(input,RULE_ID,FOLLOW_2); 

            					newLeafNode(otherlv_5, grammarAccess.getEdgeAccess().getTargetPortCrossReference_5_0());
            				

            }


            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleEdge"


    // $ANTLR start "entryRuleEBoolean"
    // InternalMediaFlow.g:990:1: entryRuleEBoolean returns [String current=null] : iv_ruleEBoolean= ruleEBoolean EOF ;
    public final String entryRuleEBoolean() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleEBoolean = null;


        try {
            // InternalMediaFlow.g:990:48: (iv_ruleEBoolean= ruleEBoolean EOF )
            // InternalMediaFlow.g:991:2: iv_ruleEBoolean= ruleEBoolean EOF
            {
             newCompositeNode(grammarAccess.getEBooleanRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleEBoolean=ruleEBoolean();

            state._fsp--;

             current =iv_ruleEBoolean.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleEBoolean"


    // $ANTLR start "ruleEBoolean"
    // InternalMediaFlow.g:997:1: ruleEBoolean returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'true' | kw= 'false' ) ;
    public final AntlrDatatypeRuleToken ruleEBoolean() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;


        	enterRule();

        try {
            // InternalMediaFlow.g:1003:2: ( (kw= 'true' | kw= 'false' ) )
            // InternalMediaFlow.g:1004:2: (kw= 'true' | kw= 'false' )
            {
            // InternalMediaFlow.g:1004:2: (kw= 'true' | kw= 'false' )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==36) ) {
                alt8=1;
            }
            else if ( (LA8_0==37) ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // InternalMediaFlow.g:1005:3: kw= 'true'
                    {
                    kw=(Token)match(input,36,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getEBooleanAccess().getTrueKeyword_0());
                    		

                    }
                    break;
                case 2 :
                    // InternalMediaFlow.g:1011:3: kw= 'false'
                    {
                    kw=(Token)match(input,37,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getEBooleanAccess().getFalseKeyword_1());
                    		

                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleEBoolean"


    // $ANTLR start "ruleMediaType"
    // InternalMediaFlow.g:1020:1: ruleMediaType returns [Enumerator current=null] : ( (enumLiteral_0= 'VIDEO' ) | (enumLiteral_1= 'AUDIO' ) | (enumLiteral_2= 'SUBTITLE' ) | (enumLiteral_3= 'IMAGE' ) | (enumLiteral_4= 'IMAGE_SEQUENCE' ) ) ;
    public final Enumerator ruleMediaType() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;
        Token enumLiteral_3=null;
        Token enumLiteral_4=null;


        	enterRule();

        try {
            // InternalMediaFlow.g:1026:2: ( ( (enumLiteral_0= 'VIDEO' ) | (enumLiteral_1= 'AUDIO' ) | (enumLiteral_2= 'SUBTITLE' ) | (enumLiteral_3= 'IMAGE' ) | (enumLiteral_4= 'IMAGE_SEQUENCE' ) ) )
            // InternalMediaFlow.g:1027:2: ( (enumLiteral_0= 'VIDEO' ) | (enumLiteral_1= 'AUDIO' ) | (enumLiteral_2= 'SUBTITLE' ) | (enumLiteral_3= 'IMAGE' ) | (enumLiteral_4= 'IMAGE_SEQUENCE' ) )
            {
            // InternalMediaFlow.g:1027:2: ( (enumLiteral_0= 'VIDEO' ) | (enumLiteral_1= 'AUDIO' ) | (enumLiteral_2= 'SUBTITLE' ) | (enumLiteral_3= 'IMAGE' ) | (enumLiteral_4= 'IMAGE_SEQUENCE' ) )
            int alt9=5;
            switch ( input.LA(1) ) {
            case 38:
                {
                alt9=1;
                }
                break;
            case 39:
                {
                alt9=2;
                }
                break;
            case 40:
                {
                alt9=3;
                }
                break;
            case 41:
                {
                alt9=4;
                }
                break;
            case 42:
                {
                alt9=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }

            switch (alt9) {
                case 1 :
                    // InternalMediaFlow.g:1028:3: (enumLiteral_0= 'VIDEO' )
                    {
                    // InternalMediaFlow.g:1028:3: (enumLiteral_0= 'VIDEO' )
                    // InternalMediaFlow.g:1029:4: enumLiteral_0= 'VIDEO'
                    {
                    enumLiteral_0=(Token)match(input,38,FOLLOW_2); 

                    				current = grammarAccess.getMediaTypeAccess().getVIDEOEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_0, grammarAccess.getMediaTypeAccess().getVIDEOEnumLiteralDeclaration_0());
                    			

                    }


                    }
                    break;
                case 2 :
                    // InternalMediaFlow.g:1036:3: (enumLiteral_1= 'AUDIO' )
                    {
                    // InternalMediaFlow.g:1036:3: (enumLiteral_1= 'AUDIO' )
                    // InternalMediaFlow.g:1037:4: enumLiteral_1= 'AUDIO'
                    {
                    enumLiteral_1=(Token)match(input,39,FOLLOW_2); 

                    				current = grammarAccess.getMediaTypeAccess().getAUDIOEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_1, grammarAccess.getMediaTypeAccess().getAUDIOEnumLiteralDeclaration_1());
                    			

                    }


                    }
                    break;
                case 3 :
                    // InternalMediaFlow.g:1044:3: (enumLiteral_2= 'SUBTITLE' )
                    {
                    // InternalMediaFlow.g:1044:3: (enumLiteral_2= 'SUBTITLE' )
                    // InternalMediaFlow.g:1045:4: enumLiteral_2= 'SUBTITLE'
                    {
                    enumLiteral_2=(Token)match(input,40,FOLLOW_2); 

                    				current = grammarAccess.getMediaTypeAccess().getSUBTITLEEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_2, grammarAccess.getMediaTypeAccess().getSUBTITLEEnumLiteralDeclaration_2());
                    			

                    }


                    }
                    break;
                case 4 :
                    // InternalMediaFlow.g:1052:3: (enumLiteral_3= 'IMAGE' )
                    {
                    // InternalMediaFlow.g:1052:3: (enumLiteral_3= 'IMAGE' )
                    // InternalMediaFlow.g:1053:4: enumLiteral_3= 'IMAGE'
                    {
                    enumLiteral_3=(Token)match(input,41,FOLLOW_2); 

                    				current = grammarAccess.getMediaTypeAccess().getIMAGEEnumLiteralDeclaration_3().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_3, grammarAccess.getMediaTypeAccess().getIMAGEEnumLiteralDeclaration_3());
                    			

                    }


                    }
                    break;
                case 5 :
                    // InternalMediaFlow.g:1060:3: (enumLiteral_4= 'IMAGE_SEQUENCE' )
                    {
                    // InternalMediaFlow.g:1060:3: (enumLiteral_4= 'IMAGE_SEQUENCE' )
                    // InternalMediaFlow.g:1061:4: enumLiteral_4= 'IMAGE_SEQUENCE'
                    {
                    enumLiteral_4=(Token)match(input,42,FOLLOW_2); 

                    				current = grammarAccess.getMediaTypeAccess().getIMAGE_SEQUENCEEnumLiteralDeclaration_4().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_4, grammarAccess.getMediaTypeAccess().getIMAGE_SEQUENCEEnumLiteralDeclaration_4());
                    			

                    }


                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleMediaType"


    // $ANTLR start "ruleBackend"
    // InternalMediaFlow.g:1071:1: ruleBackend returns [Enumerator current=null] : ( (enumLiteral_0= 'FFMPEG' ) | (enumLiteral_1= 'GSTREAMER' ) | (enumLiteral_2= 'CUSTOM' ) ) ;
    public final Enumerator ruleBackend() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;


        	enterRule();

        try {
            // InternalMediaFlow.g:1077:2: ( ( (enumLiteral_0= 'FFMPEG' ) | (enumLiteral_1= 'GSTREAMER' ) | (enumLiteral_2= 'CUSTOM' ) ) )
            // InternalMediaFlow.g:1078:2: ( (enumLiteral_0= 'FFMPEG' ) | (enumLiteral_1= 'GSTREAMER' ) | (enumLiteral_2= 'CUSTOM' ) )
            {
            // InternalMediaFlow.g:1078:2: ( (enumLiteral_0= 'FFMPEG' ) | (enumLiteral_1= 'GSTREAMER' ) | (enumLiteral_2= 'CUSTOM' ) )
            int alt10=3;
            switch ( input.LA(1) ) {
            case 43:
                {
                alt10=1;
                }
                break;
            case 44:
                {
                alt10=2;
                }
                break;
            case 45:
                {
                alt10=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }

            switch (alt10) {
                case 1 :
                    // InternalMediaFlow.g:1079:3: (enumLiteral_0= 'FFMPEG' )
                    {
                    // InternalMediaFlow.g:1079:3: (enumLiteral_0= 'FFMPEG' )
                    // InternalMediaFlow.g:1080:4: enumLiteral_0= 'FFMPEG'
                    {
                    enumLiteral_0=(Token)match(input,43,FOLLOW_2); 

                    				current = grammarAccess.getBackendAccess().getFFMPEGEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_0, grammarAccess.getBackendAccess().getFFMPEGEnumLiteralDeclaration_0());
                    			

                    }


                    }
                    break;
                case 2 :
                    // InternalMediaFlow.g:1087:3: (enumLiteral_1= 'GSTREAMER' )
                    {
                    // InternalMediaFlow.g:1087:3: (enumLiteral_1= 'GSTREAMER' )
                    // InternalMediaFlow.g:1088:4: enumLiteral_1= 'GSTREAMER'
                    {
                    enumLiteral_1=(Token)match(input,44,FOLLOW_2); 

                    				current = grammarAccess.getBackendAccess().getGSTREAMEREnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_1, grammarAccess.getBackendAccess().getGSTREAMEREnumLiteralDeclaration_1());
                    			

                    }


                    }
                    break;
                case 3 :
                    // InternalMediaFlow.g:1095:3: (enumLiteral_2= 'CUSTOM' )
                    {
                    // InternalMediaFlow.g:1095:3: (enumLiteral_2= 'CUSTOM' )
                    // InternalMediaFlow.g:1096:4: enumLiteral_2= 'CUSTOM'
                    {
                    enumLiteral_2=(Token)match(input,45,FOLLOW_2); 

                    				current = grammarAccess.getBackendAccess().getCUSTOMEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_2, grammarAccess.getBackendAccess().getCUSTOMEnumLiteralDeclaration_2());
                    			

                    }


                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleBackend"


    // $ANTLR start "ruleDirection"
    // InternalMediaFlow.g:1106:1: ruleDirection returns [Enumerator current=null] : ( (enumLiteral_0= 'IN' ) | (enumLiteral_1= 'OUT' ) ) ;
    public final Enumerator ruleDirection() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;


        	enterRule();

        try {
            // InternalMediaFlow.g:1112:2: ( ( (enumLiteral_0= 'IN' ) | (enumLiteral_1= 'OUT' ) ) )
            // InternalMediaFlow.g:1113:2: ( (enumLiteral_0= 'IN' ) | (enumLiteral_1= 'OUT' ) )
            {
            // InternalMediaFlow.g:1113:2: ( (enumLiteral_0= 'IN' ) | (enumLiteral_1= 'OUT' ) )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==46) ) {
                alt11=1;
            }
            else if ( (LA11_0==47) ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // InternalMediaFlow.g:1114:3: (enumLiteral_0= 'IN' )
                    {
                    // InternalMediaFlow.g:1114:3: (enumLiteral_0= 'IN' )
                    // InternalMediaFlow.g:1115:4: enumLiteral_0= 'IN'
                    {
                    enumLiteral_0=(Token)match(input,46,FOLLOW_2); 

                    				current = grammarAccess.getDirectionAccess().getINEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_0, grammarAccess.getDirectionAccess().getINEnumLiteralDeclaration_0());
                    			

                    }


                    }
                    break;
                case 2 :
                    // InternalMediaFlow.g:1122:3: (enumLiteral_1= 'OUT' )
                    {
                    // InternalMediaFlow.g:1122:3: (enumLiteral_1= 'OUT' )
                    // InternalMediaFlow.g:1123:4: enumLiteral_1= 'OUT'
                    {
                    enumLiteral_1=(Token)match(input,47,FOLLOW_2); 

                    				current = grammarAccess.getDirectionAccess().getOUTEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_1, grammarAccess.getDirectionAccess().getOUTEnumLiteralDeclaration_1());
                    			

                    }


                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDirection"

    // Delegated rules


 

    public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x0000000208206000L});
    public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x0000000200002000L});
    public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x000007C000000000L});
    public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_12 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x0000003000000000L});
    public static final BitSet FOLLOW_16 = new BitSet(new long[]{0x0000000100002000L});
    public static final BitSet FOLLOW_17 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_18 = new BitSet(new long[]{0x0000380000000000L});
    public static final BitSet FOLLOW_19 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_20 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_21 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_22 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_23 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_24 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_25 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_26 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_27 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_28 = new BitSet(new long[]{0x0000C00000000000L});
    public static final BitSet FOLLOW_29 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_30 = new BitSet(new long[]{0x0000000800000000L});

}