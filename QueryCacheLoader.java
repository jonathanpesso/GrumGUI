/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientWindow;

import Utils.HttpUtil;
import Utils.StringPair;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

import com.google.common.cache.CacheLoader;
import crypto.AESCTR;
import crypto.SHA256;

//import edu.ucsb.hopefully_unhackable.crypto.AESCTR;
//import edu.ucsb.hopefully_unhackable.crypto.SHA256;
//import edu.ucsb.hopefully_unhackable.utils.HttpUtil;
//import edu.ucsb.hopefully_unhackable.utils.StringPair;

public class QueryCacheLoader extends CacheLoader<String, Set<StringPair>> {
	@Override
	public Set<StringPair> load(String key) throws Exception {
		SecretKey kE = SHA256.createIndexingKey(AESCTR.secretKey, key);
		// remove + signs TEMP FIX TODO
		String encWord = SHA256.createIndexingString(kE, key).replace("+", "X");
		Set<StringPair> inds = HttpUtil.HttpGet(encWord);
		// Decrypt all inds and add to listSet
		Set<StringPair> decInds = new HashSet<>();
		for (StringPair ind : inds) {
			decInds.add(AESCTR.decrypt(ind, kE));
		}
		return decInds;
	}
}
