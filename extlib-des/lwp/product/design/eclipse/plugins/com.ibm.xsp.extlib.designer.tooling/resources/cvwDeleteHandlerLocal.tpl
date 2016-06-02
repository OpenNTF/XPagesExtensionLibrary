var unid = context.getSubmittedValue();
var doc = database.getDocumentByUNID(unid);
if (doc != null) {
    doc.remove(false);
}
