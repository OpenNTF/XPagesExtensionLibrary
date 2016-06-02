var unid = context.getSubmittedValue();
var doc = session.getDatabase("%SERVER-NAME%", "%DATABASE-NAME%").getDocumentByUNID(unid);
if (doc != null) {
    doc.remove(false);
}